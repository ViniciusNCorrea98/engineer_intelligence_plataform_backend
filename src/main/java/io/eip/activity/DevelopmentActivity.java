package io.eip.activity;

import java.time.Instant;
import java.util.UUID;

import io.eip.project.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "development_activities")
/**
 * Aggregate for a unit of engineering work and its guarded lifecycle
 * transitions.
 */
public class DevelopmentActivity {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    private String title;
    private String externalIssueReference;
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;
    private Instant createdAt;
    private Instant completedAt;
    private UUID successorActivityId;

    protected DevelopmentActivity() {
    }

    public DevelopmentActivity(UUID id, Project project, String title, String externalIssueReference) {
        this.id = id;
        this.project = project;
        this.title = title;
        this.externalIssueReference = externalIssueReference;
        this.status = ActivityStatus.OPEN;
        this.createdAt = Instant.now();
    }

    public void complete(UUID successorActivityId) {
        // A successor preserves the preferred history of new work after closure.
        if (status == ActivityStatus.COMPLETED) {
            throw new IllegalStateException("Activity is already completed");
        }
        status = ActivityStatus.COMPLETED;
        completedAt = Instant.now();
        this.successorActivityId = successorActivityId;
    }

    public void reopen() {
        // The application service records the mandatory exceptional-reopen audit.
        if (status != ActivityStatus.COMPLETED) {
            throw new IllegalStateException("Only completed activities can be reopened");
        }
        status = ActivityStatus.OPEN;
        completedAt = null;
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public String getExternalIssueReference() {
        return externalIssueReference;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public UUID getSuccessorActivityId() {
        return successorActivityId;
    }
}
