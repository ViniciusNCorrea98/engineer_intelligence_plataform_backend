package io.eip.security;

import io.eip.project.Project;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_identities")
public class ServiceIdentity {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    private String name;
    @Enumerated(EnumType.STRING)
    private ServiceIdentityState state;
    private Instant createdAt;

    protected ServiceIdentity() {
    }

    public ServiceIdentity(Project project, String name) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.name = name;
        this.state = ServiceIdentityState.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void suspend() {
        state = ServiceIdentityState.SUSPENDED;
    }

    public void activate() {
        state = ServiceIdentityState.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public ServiceIdentityState getState() {
        return state;
    }
}
