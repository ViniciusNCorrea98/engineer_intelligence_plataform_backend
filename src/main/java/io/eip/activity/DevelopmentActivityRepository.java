package io.eip.activity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DevelopmentActivityRepository extends JpaRepository<DevelopmentActivity, UUID> {

    Optional<DevelopmentActivity> findByIdAndProjectId(UUID id, UUID projectId);
}
