package io.eip.ingestion;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import io.eip.activity.DevelopmentActivity;
import io.eip.project.Project;
import io.eip.security.ProjectSource;
import io.eip.security.ServiceCredential;
import io.eip.security.ServiceIdentity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingested_evidences")
public class IngestedEvidence {

    @Id
    private UUID id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_source_id")
    private ProjectSource source;
    @ManyToOne(optional = false)
    @JoinColumn(name = "service_identity_id")
    private ServiceIdentity identity;
    @ManyToOne(optional = false)
    @JoinColumn(name = "credential_id")
    private ServiceCredential credential;
    private String eventId;
    private String idempotencyKey;
    private String schemaVersion;
    private String eventType;
    private Instant occurredAt;
    private Instant observedAt;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes_json")
    private JsonNode attributesJson;
    @ManyToOne
    @JoinColumn(name = "declared_activity_id")
    private DevelopmentActivity declaredActivity;
    private String repositoryReference;
    private Instant receivedAt;

    protected IngestedEvidence() {
    }

    public IngestedEvidence(Project p, ProjectSource s, ServiceIdentity i, ServiceCredential c, String eid, String key, String schema, String type, Instant occurred, Instant observed, JsonNode attrs, DevelopmentActivity activity, String repo) {
        id = UUID.randomUUID();
        project = p;
        source = s;
        identity = i;
        credential = c;
        eventId = eid;
        idempotencyKey = key;
        schemaVersion = schema;
        eventType = type;
        occurredAt = occurred;
        observedAt = observed;
        attributesJson = attrs;
        declaredActivity = activity;
        repositoryReference = repo;
        receivedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public JsonNode getAttributesJson() {
        return attributesJson;
    }

    public String getRepositoryReference() {
        return repositoryReference;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public DevelopmentActivity getDeclaredActivity() {
        return declaredActivity;
    }
}
