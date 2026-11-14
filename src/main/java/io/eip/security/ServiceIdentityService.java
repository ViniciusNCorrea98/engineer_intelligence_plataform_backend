package io.eip.security;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.eip.common.ForbiddenException;
import io.eip.common.NotFoundException;
import io.eip.common.UnauthorizedException;
import io.eip.project.Project;
import io.eip.project.ProjectService;

@Service
public class ServiceIdentityService {

    private final ProjectService projects;
    private final ProjectSourceRepository sources;
    private final ServiceIdentityRepository identities;
    private final ServiceCredentialRepository credentials;
    private final ServiceGrantRepository grants;
    private final AuditService audit;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    public ServiceIdentityService(ProjectService p, ProjectSourceRepository s, ServiceIdentityRepository i, ServiceCredentialRepository c, ServiceGrantRepository g, AuditService a) {
        projects = p;
        sources = s;
        identities = i;
        credentials = c;
        grants = g;
        audit = a;
    }

    @Transactional
    public ProjectSource createSource(UUID org, UUID projectId, String key, String actor) {
        Project p = projects.projectInOrganization(org, projectId);
        ProjectSource source = sources.save(new ProjectSource(p, key));
        audit.record("HUMAN", actor, "PROJECT_SOURCE_CREATED", "ProjectSource", source.getId().toString(), projectId, "SUCCESS", null);
        return source;
    }

    @Transactional
    public ServiceIdentity createIdentity(UUID org, UUID projectId, String name, String actor) {
        Project p = projects.projectInOrganization(org, projectId);
        ServiceIdentity id = identities.save(new ServiceIdentity(p, name));
        audit.record("HUMAN", actor, "SERVICE_IDENTITY_CREATED", "ServiceIdentity", id.getId().toString(), projectId, "SUCCESS", null);
        return id;
    }

    @Transactional
    public IssuedCredential issue(UUID org, UUID projectId, UUID identityId, String actor) {
        ServiceIdentity identity = identity(org, projectId, identityId);
        return createCredential(identity, actor, "SERVICE_CREDENTIAL_ISSUED");
    }

    @Transactional
    public IssuedCredential rotate(UUID org, UUID projectId, UUID identityId, UUID credentialId, String actor) {
        ServiceIdentity identity = identity(org, projectId, identityId);
        ServiceCredential old = credentials.findById(credentialId).filter(c -> c.getServiceIdentity().getId().equals(identity.getId())).orElseThrow(() -> new NotFoundException("Credential not found"));
        old.revoke();
        audit.record("HUMAN", actor, "SERVICE_CREDENTIAL_REVOKED", "ServiceCredential", credentialId.toString(), projectId, "SUCCESS", "rotation");
        return createCredential(identity, actor, "SERVICE_CREDENTIAL_ROTATED");
    }

    @Transactional
    public void revoke(UUID org, UUID projectId, UUID identityId, UUID credentialId, String actor) {
        ServiceCredential c = credentials.findById(credentialId).filter(x -> x.getServiceIdentity().getId().equals(identity(org, projectId, identityId).getId())).orElseThrow(() -> new NotFoundException("Credential not found"));
        c.revoke();
        audit.record("HUMAN", actor, "SERVICE_CREDENTIAL_REVOKED", "ServiceCredential", credentialId.toString(), projectId, "SUCCESS", null);
    }

    @Transactional
    public void setSuspended(UUID org, UUID projectId, UUID id, boolean suspended, String actor) {
        ServiceIdentity i = identity(org, projectId, id);
        if (suspended) {
            i.suspend(); 
        }else {
            i.activate();
        
        }audit.record("HUMAN", actor, suspended ? "SERVICE_IDENTITY_SUSPENDED" : "SERVICE_IDENTITY_ACTIVATED", "ServiceIdentity", id.toString(), projectId, "SUCCESS", null);
    }

    @Transactional
    public void grant(UUID org, UUID projectId, UUID identityId, String sourceKey, ServiceScope scope, String actor) {
        ServiceIdentity i = identity(org, projectId, identityId);
        ProjectSource s = source(projectId, sourceKey);
        if (!grants.existsByServiceIdentityIdAndProjectSourceIdAndScope(i.getId(), s.getId(), scope)) {
            grants.save(new ServiceGrant(i, s, scope));
        
        }audit.record("HUMAN", actor, "SERVICE_GRANT_CREATED", "ProjectSource", s.getId().toString(), projectId, "SUCCESS", scope.name());
    }

    @Transactional
    public AuthenticatedService authenticate(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer eip_si_")) {
            audit.record("UNKNOWN", null, "SERVICE_AUTHENTICATION", "Credential", null, null, "DENIED", "invalid token format");
            throw new UnauthorizedException();
        }
        String[] parts = authorization.substring(7).split("\\.", 2);
        if (parts.length != 2) {
            audit.record("UNKNOWN", null, "SERVICE_AUTHENTICATION", "Credential", null, null, "DENIED", "invalid token format");
            throw new UnauthorizedException();
        }
        UUID credentialId;
        try {
            credentialId = UUID.fromString(parts[0].replace("eip_si_", ""));
        } catch (IllegalArgumentException e) {
            audit.record("UNKNOWN", null, "SERVICE_AUTHENTICATION", "Credential", null, null, "DENIED", "invalid credential id");
            throw new UnauthorizedException();
        }
        ServiceCredential c = credentials.findById(credentialId).orElse(null);
        if (c == null || c.getState() != CredentialState.ACTIVE || c.getServiceIdentity().getState() != ServiceIdentityState.ACTIVE || !encoder.matches(parts[1], c.getSecretHash())) {
            audit.record("UNKNOWN", credentialId.toString(), "SERVICE_AUTHENTICATION", "Credential", credentialId.toString(), null, "DENIED", "invalid, revoked, or suspended credential");
            throw new UnauthorizedException();
        }
        c.used();
        return new AuthenticatedService(c.getServiceIdentity(), c);
    }

    @Transactional(readOnly = true)
    public ProjectSource authorize(AuthenticatedService principal, UUID projectId, String sourceKey) {
        if (!principal.identity().getProject().getId().equals(projectId)) {
            audit.record("SERVICE_IDENTITY", principal.identity().getId().toString(), "EVENT_INGEST", "Project", projectId.toString(), projectId, "DENIED", "project outside identity scope");
            throw new ForbiddenException();
        }
        ProjectSource source = source(projectId, sourceKey);
        if (!grants.existsByServiceIdentityIdAndProjectSourceIdAndScope(principal.identity().getId(), source.getId(), ServiceScope.EVENTS_INGEST)) {
            audit.record("SERVICE_IDENTITY", principal.identity().getId().toString(), "EVENT_INGEST", "ProjectSource", source.getId().toString(), projectId, "DENIED", "source outside grant");
            throw new ForbiddenException();
        }
        return source;
    }

    private ServiceIdentity identity(UUID org, UUID project, UUID id) {
        projects.projectInOrganization(org, project);
        return identities.findByIdAndProjectId(id, project).orElseThrow(() -> new NotFoundException("Service identity not found"));
    }

    private ProjectSource source(UUID project, String key) {
        return sources.findByProjectIdAndSourceKey(project, key).orElseThrow(() -> new NotFoundException("Project source not found"));
    }

    private IssuedCredential createCredential(ServiceIdentity identity, String actor, String action) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        ServiceCredential c = credentials.save(new ServiceCredential(identity, encoder.encode(secret)));
        audit.record("HUMAN", actor, action, "ServiceCredential", c.getId().toString(), identity.getProject().getId(), "SUCCESS", null);
        return new IssuedCredential(c.getId(), "eip_si_" + c.getId() + "." + secret);
    }

    public record IssuedCredential(UUID credentialId, String token) {}

    public record AuthenticatedService(ServiceIdentity identity, ServiceCredential credential) {}
}
