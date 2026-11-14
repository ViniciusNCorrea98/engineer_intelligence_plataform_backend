package io.eip.activity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity_reopen_audits")
public class ActivityReopenAudit {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private DevelopmentActivity activity;
    private String actorId;
    private String justification;
    private Instant recordedAt;

    protected ActivityReopenAudit() {
    }

    public ActivityReopenAudit(DevelopmentActivity activity, String actorId, String justification) {
        this.id = UUID.randomUUID();
        this.activity = activity;
        this.actorId = actorId;
        this.justification = justification;
        this.recordedAt = Instant.now();
    }
}
