package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Persisted human principal; authentication material is limited to a password hash. */
@Entity
@Table(name = "users")
public class HumanUser {
    @Id private UUID id;
    private String email;
    private String passwordHash;
    @Enumerated(EnumType.STRING) private HumanIdentityState state;
    private Instant createdAt;
    private Instant updatedAt;

    protected HumanUser() { }

    public HumanUser(String email, String passwordHash) {
        id = UUID.randomUUID();
        this.email = email;
        this.passwordHash = passwordHash;
        state = HumanIdentityState.ACTIVE;
        createdAt = Instant.now();
        updatedAt = createdAt;
    }
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public HumanIdentityState getState() { return state; }
}
