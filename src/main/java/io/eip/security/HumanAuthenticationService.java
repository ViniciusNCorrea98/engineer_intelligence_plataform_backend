package io.eip.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.eip.common.ConflictException;
import io.eip.common.UnauthorizedException;
import io.eip.project.Organization;
import io.eip.project.OrganizationRepository;

/**
 * Owns human registration and opaque server-side sessions. It deliberately does
 * not share credentials or authorization rules with ServiceIdentity.
 */
@Service
public class HumanAuthenticationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final HumanUserRepository users;
    private final OrganizationRepository organizations;
    private final OrganizationMembershipRepository organizationMemberships;
    private final HumanSessionRepository sessions;
    private final AuditService audit;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Duration sessionLifetime;

    public HumanAuthenticationService(HumanUserRepository users, OrganizationRepository organizations,
            OrganizationMembershipRepository organizationMemberships, HumanSessionRepository sessions,
            AuditService audit, @Value("${eip.human-session.ttl-hours:8}") long sessionTtlHours) {
        this.users = users; this.organizations = organizations; this.organizationMemberships = organizationMemberships;
        this.sessions = sessions; this.audit = audit; sessionLifetime = Duration.ofHours(sessionTtlHours);
    }

    /** The user, personal organization, and initial authority share one transaction boundary. */
    @Transactional
    public HumanUser register(String submittedEmail, String password) {
        String email = normalizeEmail(submittedEmail);
        if (users.findByEmail(email).isPresent()) throw new ConflictException("Email already registered");
        HumanUser user = users.save(new HumanUser(email, encoder.encode(password)));
        Organization personalOrganization = organizations.save(new Organization(UUID.randomUUID(), "Personal organization"));
        organizationMemberships.save(new OrganizationMembership(user, personalOrganization, HumanRole.ORG_ADMIN));
        audit.record("HUMAN", user.getId().toString(), "USER_REGISTERED", "USER", user.getId().toString(), null, "SUCCESS", null);
        return user;
    }

    @Transactional
    public IssuedSession login(String submittedEmail, String password) {
        String email = normalizeEmail(submittedEmail);
        HumanUser user = users.findByEmail(email).orElse(null);
        if (user == null || user.getState() != HumanIdentityState.ACTIVE || !encoder.matches(password, user.getPasswordHash())) {
            audit.record("ANONYMOUS", null, "HUMAN_LOGIN", "SESSION", null, null, "DENIED", "Invalid credentials");
            throw new UnauthorizedException();
        }
        String secret = randomValue();
        String csrf = randomValue();
        HumanSession session = sessions.save(new HumanSession(user, encoder.encode(secret), encoder.encode(csrf), Instant.now().plus(sessionLifetime)));
        audit.record("HUMAN", user.getId().toString(), "HUMAN_LOGIN", "SESSION", session.getId().toString(), null, "SUCCESS", null);
        return new IssuedSession("eip_hs_" + session.getId() + "." + secret, csrf, sessionLifetime);
    }

    @Transactional(readOnly = true)
    public AuthenticatedHuman authenticate(String token) {
        ParsedToken parsed = parse(token);
        HumanSession session = sessions.findById(parsed.id()).orElseThrow(() -> denied("Invalid session"));
        if (!session.isUsable(Instant.now()) || session.getUser().getState() != HumanIdentityState.ACTIVE || !session.matchesSecret(encoder, parsed.secret())) {
            throw denied("Invalid session");
        }
        return new AuthenticatedHuman(session.getUser(), session);
    }

    /** Unsafe HTTP methods must present the independent CSRF value bound to this session. */
    @Transactional(readOnly = true)
    public AuthenticatedHuman authenticateForWrite(String token, String csrf) {
        AuthenticatedHuman authenticated = authenticate(token);
        if (csrf == null || !authenticated.session().matchesCsrf(encoder, csrf)) throw denied("Invalid CSRF token");
        return authenticated;
    }

    @Transactional
    public void logout(String token, String csrf) {
        AuthenticatedHuman authenticated = authenticateForWrite(token, csrf);
        authenticated.session().revoke();
        audit.record("HUMAN", authenticated.user().getId().toString(), "HUMAN_LOGOUT", "SESSION", authenticated.session().getId().toString(), null, "SUCCESS", null);
    }

    private UnauthorizedException denied(String reason) {
        audit.record("ANONYMOUS", null, "HUMAN_SESSION", "SESSION", null, null, "DENIED", reason);
        return new UnauthorizedException();
    }
    private String normalizeEmail(String email) { return email.trim().toLowerCase(Locale.ROOT); }
    private String randomValue() { byte[] value = new byte[32]; RANDOM.nextBytes(value); return Base64.getUrlEncoder().withoutPadding().encodeToString(value); }
    private ParsedToken parse(String token) {
        if (token == null || !token.startsWith("eip_hs_")) throw denied("Missing session");
        String[] parts = token.substring("eip_hs_".length()).split("\\.", 2);
        try { if (parts.length != 2 || parts[1].isBlank()) throw new IllegalArgumentException(); return new ParsedToken(UUID.fromString(parts[0]), parts[1]); }
        catch (IllegalArgumentException exception) { throw denied("Malformed session"); }
    }
    private record ParsedToken(UUID id, String secret) { }
    public record AuthenticatedHuman(HumanUser user, HumanSession session) { }
    public record IssuedSession(String token, String csrfToken, Duration ttl) { }
}
