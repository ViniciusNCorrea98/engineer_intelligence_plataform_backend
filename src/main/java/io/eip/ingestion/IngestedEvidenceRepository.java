package io.eip.ingestion;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestedEvidenceRepository extends JpaRepository<IngestedEvidence, UUID> {

    Optional<IngestedEvidence> findByIdentityIdAndIdempotencyKey(UUID identityId, String key);

    Optional<IngestedEvidence> findByProjectIdAndEventId(UUID projectId, String eventId);

    long countByProjectId(UUID projectId);
}
