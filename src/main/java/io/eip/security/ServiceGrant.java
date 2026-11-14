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
@Table(name = "service_grants")
public class ServiceGrant {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "service_identity_id")
    private ServiceIdentity serviceIdentity;
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_source_id")
    private ProjectSource projectSource;
    @Enumerated(EnumType.STRING)
    private ServiceScope scope;
    private Instant createdAt;

    protected ServiceGrant() {
    }

    public ServiceGrant(ServiceIdentity identity, ProjectSource source, ServiceScope scope) {
        id = UUID.randomUUID();
        serviceIdentity = identity;
        projectSource = source;
        this.scope = scope;
        createdAt = Instant.now();
    }
}
