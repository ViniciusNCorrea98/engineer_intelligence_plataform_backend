package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Server-side session state. Raw session and CSRF values never reach persistence. */
@Entity
@Table(name = "human_sessions")
public class HumanSession {
    @Id private UUID id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id") private HumanUser user;
    private String secretHash;
    private String csrfHash;
    private Instant expiresAt;
    private Instant revokedAt;
    private Instant createdAt;
    protected HumanSession() { }
    public HumanSession(HumanUser user, String secretHash, String csrfHash, Instant expiresAt) {
        id = UUID.randomUUID(); this.user = user; this.secretHash = secretHash; this.csrfHash = csrfHash;
        this.expiresAt = expiresAt; createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public HumanUser getUser() { return user; }
    public boolean isUsable(Instant now) { return revokedAt == null && expiresAt.isAfter(now); }
    public boolean matchesSecret(org.springframework.security.crypto.password.PasswordEncoder encoder, String secret) { return encoder.matches(secret, secretHash); }
    public boolean matchesCsrf(org.springframework.security.crypto.password.PasswordEncoder encoder, String csrf) { return encoder.matches(csrf, csrfHash); }
    public void revoke() { if (revokedAt == null) revokedAt = Instant.now(); }
}
