package io.eip.activity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/projects/{projectId}/activities")
public class ActivityController {

    private final ActivityService activities;

    public ActivityController(ActivityService activities) {
        this.activities = activities;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ActivityResponse create(@PathVariable UUID projectId, @RequestHeader("X-Organization-Id") UUID organizationId,
            @Valid @RequestBody CreateActivityRequest request) {
        return ActivityResponse.from(activities.create(organizationId, projectId, request.title(), request.externalIssueReference()));
    }

    @GetMapping("/{activityId}")
    ActivityResponse get(@PathVariable UUID projectId, @PathVariable UUID activityId,
            @RequestHeader("X-Organization-Id") UUID organizationId) {
        return ActivityResponse.from(activities.get(organizationId, projectId, activityId));
    }

    @PostMapping("/{activityId}/complete")
    ActivityResponse complete(@PathVariable UUID projectId, @PathVariable UUID activityId,
            @RequestHeader("X-Organization-Id") UUID organizationId, @Valid @RequestBody CompleteActivityRequest request) {
        return ActivityResponse.from(activities.complete(organizationId, projectId, activityId, request.successorTitle()));
    }

    @PostMapping("/{activityId}/reopen")
    ActivityResponse reopen(@PathVariable UUID projectId, @PathVariable UUID activityId,
            @RequestHeader("X-Organization-Id") UUID organizationId, @Valid @RequestBody ReopenActivityRequest request) {
        return ActivityResponse.from(activities.reopen(organizationId, projectId, activityId, request.actorId(), request.justification()));
    }

    @PostMapping("/{activityId}/evidence-associations")
    @ResponseStatus(HttpStatus.CREATED)
    AssociationResponse declare(@PathVariable UUID projectId, @PathVariable UUID activityId,
            @RequestHeader("X-Organization-Id") UUID organizationId, @Valid @RequestBody DeclareAssociationRequest request) {
        return AssociationResponse.from(activities.declareAssociation(organizationId, projectId, activityId,
                request.sourceSystem(), request.repositoryReference(), request.evidenceReference(), request.declaredBy(), request.confidence()));
    }

    @PostMapping("/{activityId}/evidence-associations/{associationId}/decision")
    AssociationResponse decide(@PathVariable UUID projectId, @PathVariable UUID activityId, @PathVariable UUID associationId,
            @RequestHeader("X-Organization-Id") UUID organizationId, @Valid @RequestBody DecideAssociationRequest request) {
        return AssociationResponse.from(activities.decideAssociation(organizationId, projectId, activityId, associationId,
                request.state(), request.actorId(), request.reason()));
    }

    public record CreateActivityRequest(@NotBlank String title, String externalIssueReference) {

    }

    public record CompleteActivityRequest(String successorTitle) {

    }

    public record ReopenActivityRequest(@NotBlank String actorId, @NotBlank String justification) {

    }

    public record DeclareAssociationRequest(@NotBlank String sourceSystem, @NotBlank String repositoryReference,
            @NotBlank String evidenceReference, @NotBlank String declaredBy,
            @DecimalMin("0.0")
            @DecimalMax("1.0") Double confidence) {

    }

    public record DecideAssociationRequest(@NotNull AssociationState state, @NotBlank String actorId, String reason) {

    }

    public record ActivityResponse(UUID id, UUID projectId, String title, String externalIssueReference,
            ActivityStatus status, Instant createdAt, Instant completedAt, UUID successorActivityId) {

        static ActivityResponse from(DevelopmentActivity value) {
            return new ActivityResponse(value.getId(), value.getProject().getId(), value.getTitle(), value.getExternalIssueReference(),
                    value.getStatus(), value.getCreatedAt(), value.getCompletedAt(), value.getSuccessorActivityId());
        }
    }

    public record AssociationResponse(UUID id, UUID activityId, String sourceSystem, String repositoryReference,
            String evidenceReference, String declaredBy, Double confidence, AssociationState state, Instant declaredAt,
            Instant decidedAt, String decidedBy, String decisionReason, Instant canonicalRelationEstablishedAt) {

        static AssociationResponse from(EvidenceAssociation value) {
            return new AssociationResponse(value.getId(), value.getActivity().getId(), value.getSourceSystem(),
                    value.getRepositoryReference(), value.getEvidenceReference(), value.getDeclaredBy(), value.getConfidence(),
                    value.getState(), value.getDeclaredAt(), value.getDecidedAt(), value.getDecidedBy(), value.getDecisionReason(),
                    value.getCanonicalRelationEstablishedAt());
        }
    }
}
