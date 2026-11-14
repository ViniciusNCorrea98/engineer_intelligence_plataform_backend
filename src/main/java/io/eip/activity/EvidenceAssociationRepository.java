package io.eip.activity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenceAssociationRepository extends JpaRepository<EvidenceAssociation, UUID> {

    Optional<EvidenceAssociation> findByIdAndActivityId(UUID id, UUID activityId);
}
