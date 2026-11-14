package io.eip.activity;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.eip.common.ConflictException;
import io.eip.common.NotFoundException;
import io.eip.project.Project;
import io.eip.project.ProjectService;

@Service
public class ActivityService {

    private final ProjectService projects;
    private final DevelopmentActivityRepository activities;
    private final ActivityReopenAuditRepository reopenAudits;
    private final EvidenceAssociationRepository associations;

    public ActivityService(ProjectService projects, DevelopmentActivityRepository activities,
            ActivityReopenAuditRepository reopenAudits, EvidenceAssociationRepository associations) {
        this.projects = projects;
        this.activities = activities;
        this.reopenAudits = reopenAudits;
        this.associations = associations;
    }

    @Transactional
    public DevelopmentActivity create(UUID organizationId, UUID projectId, String title, String externalIssueReference) {
        Project project = projects.projectInOrganization(organizationId, projectId);
        return activities.save(new DevelopmentActivity(UUID.randomUUID(), project, title, externalIssueReference));
    }

    @Transactional(readOnly = true)
    public DevelopmentActivity get(UUID organizationId, UUID projectId, UUID activityId) {
        projects.projectInOrganization(organizationId, projectId);
        return activities.findByIdAndProjectId(activityId, projectId)
                .orElseThrow(() -> new NotFoundException("Activity not found"));
    }

    @Transactional
    public DevelopmentActivity complete(UUID organizationId, UUID projectId, UUID activityId, String successorTitle) {
        DevelopmentActivity activity = get(organizationId, projectId, activityId);
        UUID successorId = null;
        if (successorTitle != null && !successorTitle.isBlank()) {
            DevelopmentActivity successor = activities.save(new DevelopmentActivity(
                    UUID.randomUUID(), activity.getProject(), successorTitle, null));
            successorId = successor.getId();
        }
        try {
            activity.complete(successorId);
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
        return activity;
    }

    @Transactional
    public DevelopmentActivity reopen(UUID organizationId, UUID projectId, UUID activityId, String actorId, String justification) {
        DevelopmentActivity activity = get(organizationId, projectId, activityId);
        try {
            activity.reopen();
        } catch (IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
        reopenAudits.save(new ActivityReopenAudit(activity, actorId, justification));
        return activity;
    }

    @Transactional
    public EvidenceAssociation declareAssociation(UUID organizationId, UUID projectId, UUID activityId,
            String sourceSystem, String repositoryReference, String evidenceReference, String declaredBy, Double confidence) {
        DevelopmentActivity activity = get(organizationId, projectId, activityId);
        return associations.save(new EvidenceAssociation(activity, sourceSystem, repositoryReference,
                evidenceReference, declaredBy, confidence));
    }

    @Transactional
    public EvidenceAssociation decideAssociation(UUID organizationId, UUID projectId, UUID activityId, UUID associationId,
            AssociationState decision, String actorId, String reason) {
        get(organizationId, projectId, activityId);
        EvidenceAssociation association = associations.findByIdAndActivityId(associationId, activityId)
                .orElseThrow(() -> new NotFoundException("Evidence association not found"));
        try {
            association.decide(decision, actorId, reason);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            throw new ConflictException(exception.getMessage());
        }
        return association;
    }
}
