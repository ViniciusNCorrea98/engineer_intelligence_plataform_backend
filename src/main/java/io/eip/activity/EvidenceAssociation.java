package io.eip.activity;

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
@Table(name = "evidence_associations")
/**
 * A declared link is not canonical until an explicit confirmation is recorded.
 */
public class EvidenceAssociation {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private DevelopmentActivity activity;
    private String sourceSystem;
    private String repositoryReference;
    private String evidenceReference;
    private String declaredBy;
    private Double confidence;
    @Enumerated(EnumType.STRING)
    private AssociationState state;
    private Instant declaredAt;
    private Instant decidedAt;
    private String decidedBy;
    private String decisionReason;
    private Instant canonicalRelationEstablishedAt;

    protected EvidenceAssociation() {
    }

    public EvidenceAssociation(DevelopmentActivity activity, String sourceSystem, String repositoryReference,
            String evidenceReference, String declaredBy, Double confidence) {
        this.id = UUID.randomUUID();
        this.activity = activity;
        this.sourceSystem = sourceSystem;
        this.repositoryReference = repositoryReference;
        this.evidenceReference = evidenceReference;
        this.declaredBy = declaredBy;
        this.confidence = confidence;
        this.state = AssociationState.DECLARED;
        this.declaredAt = Instant.now();
    }

    public void decide(AssociationState decision, String actorId, String reason) {
        // Only CONFIRMED establishes the canonical relation.
        if (state != AssociationState.DECLARED) {
            throw new IllegalStateException("Association has already been decided");
        }
        if (decision == AssociationState.DECLARED) {
            throw new IllegalArgumentException("Decision must confirm or reject");
        }
        if (decision == AssociationState.REJECTED && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException("Rejected associations require a reason");
        }
        state = decision;
        decidedBy = actorId;
        decisionReason = reason;
        decidedAt = Instant.now();
        if (decision == AssociationState.CONFIRMED) {
            canonicalRelationEstablishedAt = decidedAt;
        }
    }

    public UUID getId() {
        return id;
    }

    public DevelopmentActivity getActivity() {
        return activity;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public String getRepositoryReference() {
        return repositoryReference;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public String getDeclaredBy() {
        return declaredBy;
    }

    public Double getConfidence() {
        return confidence;
    }

    public AssociationState getState() {
        return state;
    }

    public Instant getDeclaredAt() {
        return declaredAt;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public String getDecidedBy() {
        return decidedBy;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public Instant getCanonicalRelationEstablishedAt() {
        return canonicalRelationEstablishedAt;
    }
}
