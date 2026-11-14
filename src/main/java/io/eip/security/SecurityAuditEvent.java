package io.eip.security;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "security_audit_events")
public class SecurityAuditEvent {

    @Id
    private UUID id;
    private String actorType;
    private String actorId;
    private String action;
    private String targetType;
    private String targetId;
    private UUID projectId;
    private String outcome;
    private String reason;
    private Instant occurredAt;

    protected SecurityAuditEvent() {
    }

    public SecurityAuditEvent(String actorType, String actorId, String action, String targetType, String targetId, UUID projectId, String outcome, String reason) {
        id = UUID.randomUUID();
        this.actorType = actorType;
        this.actorId = actorId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.projectId = projectId;
        this.outcome = outcome;
        this.reason = reason;
        occurredAt = Instant.now();
    }
}
