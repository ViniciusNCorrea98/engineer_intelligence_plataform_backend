package io.eip.ingestion;

import java.time.Instant;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import io.eip.activity.DevelopmentActivity;
import io.eip.activity.DevelopmentActivityRepository;
import io.eip.activity.EvidenceAssociation;
import io.eip.activity.EvidenceAssociationRepository;
import io.eip.common.ConflictException;
import io.eip.common.NotFoundException;
import io.eip.project.Project;
import io.eip.project.ProjectService;
import io.eip.security.AuditService;
import io.eip.security.ProjectSource;
import io.eip.security.ServiceIdentityService;

@Service
/**
 * Enforces the collector trust boundary and idempotent evidence persistence.
 */
public class IngestionService {

    private final ServiceIdentityService services;
    private final ProjectService projects;
    private final DevelopmentActivityRepository activities;
    private final EvidenceAssociationRepository associations;
    private final IngestedEvidenceRepository evidences;
    private final EvidencePersistence persistence;
    private final AuditService audit;

    public IngestionService(ServiceIdentityService s, ProjectService p, DevelopmentActivityRepository a, EvidenceAssociationRepository ar, IngestedEvidenceRepository e, EvidencePersistence ep, AuditService au) {
        services = s;
        projects = p;
        activities = a;
        associations = ar;
        evidences = e;
        persistence = ep;
        audit = au;
    }

    @Transactional
    public Result ingest(String authorization, String key, Request request) {
        // Validate caller-controlled retry data before any durable side effect.
        if (key == null || key.isBlank() || key.length() > 255) {
            throw new IllegalArgumentException("A valid idempotency key is required");
        
        }if (request.attributes() == null || !request.attributes().isObject()) {
            throw new IllegalArgumentException("attributes must be an object");
        }
        ServiceIdentityService.AuthenticatedService principal = services.authenticate(authorization);
        ProjectSource source = services.authorize(principal, request.projectId(), request.sourceKey());
        Project project = projects.projectInOrganization(principal.identity().getProject().getOrganization().getId(), request.projectId());
        DevelopmentActivity activity = null;
        if (request.correlation() != null && request.correlation().activityId() != null) {
            activity = activities.findByIdAndProjectId(request.correlation().activityId(), project.getId()).orElseThrow(() -> new NotFoundException("Declared activity not found"));
        }
        IngestedEvidence evidence = new IngestedEvidence(project, source, principal.identity(), principal.credential(), request.eventId(), key, request.schemaVersion(), request.eventType(), request.occurredAt(), request.observedAt(), request.attributes(), activity, request.repositoryReference());
        // Database uniqueness, not an in-memory check, protects concurrent retries.
        try {
            persistence.insert(evidence);
        } catch (DataIntegrityViolationException exception) {
            return duplicate(principal.identity().getId(), project.getId(), key, request.eventId());
        }
        // Collector-supplied correlation remains DECLARED; ingestion never canonicalizes it.
        if (activity != null) {
            associations.save(new EvidenceAssociation(activity, source.getSourceKey(), request.repositoryReference() == null ? "unknown" : request.repositoryReference(), request.eventId(), "service-identity:" + principal.identity().getId(), request.correlation().confidence()));
        }
        audit.record("SERVICE_IDENTITY", principal.identity().getId().toString(), "EVENT_INGEST", "Evidence", evidence.getId().toString(), project.getId(), "SUCCESS", null);
        return new Result(evidence.getId(), false);
    }

    private Result duplicate(UUID identityId, UUID projectId, String key, String eventId) {
        IngestedEvidence existing = evidences.findByIdentityIdAndIdempotencyKey(identityId, key).or(() -> evidences.findByProjectIdAndEventId(projectId, eventId)).orElseThrow(() -> new ConflictException("Duplicate event could not be resolved"));
        return new Result(existing.getId(), true);
    }

    public record Request(String schemaVersion, UUID projectId, String sourceKey, String eventId, String eventType, Instant occurredAt, Instant observedAt, JsonNode attributes, Correlation correlation, String repositoryReference) {}

    public record Correlation(UUID activityId, Double confidence) {}

    public record Result(UUID evidenceId, boolean duplicate) {}
}
