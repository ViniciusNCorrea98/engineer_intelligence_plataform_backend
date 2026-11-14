package io.eip.security;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
/** Writes security audit facts independently from business transactions and operational logs. */
public class AuditService {

    private final SecurityAuditRepository audits;

    public AuditService(SecurityAuditRepository audits) {
        this.audits = audits;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String actorType, String actorId, String action, String targetType, String targetId, UUID projectId, String outcome, String reason) {
        // A denied request remains auditable even when its caller transaction rolls back.
        audits.save(new SecurityAuditEvent(actorType, actorId, action, targetType, targetId, projectId, outcome, reason));
    }
}
