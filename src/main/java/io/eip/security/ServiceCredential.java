package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_credentials")
public class ServiceCredential {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "service_identity_id")
    private ServiceIdentity serviceIdentity;
    private String secretHash;
    @Enumerated(EnumType.STRING)
    private CredentialState state;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant lastUsedAt;
    private Instant revokedAt;

    protected ServiceCredential() {
    }

    public ServiceCredential(ServiceIdentity identity, String hash) {
        id = UUID.randomUUID();
        serviceIdentity = identity;
        secretHash = hash;
        state = CredentialState.ACTIVE;
        createdAt = Instant.now();
    }

    public void revoke() {
        state = CredentialState.REVOKED;
        revokedAt = Instant.now();
    }

    public void used() {
        lastUsedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public ServiceIdentity getServiceIdentity() {
        return serviceIdentity;
    }

    public String getSecretHash() {
        return secretHash;
    }

    public CredentialState getState() {
        return state;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
