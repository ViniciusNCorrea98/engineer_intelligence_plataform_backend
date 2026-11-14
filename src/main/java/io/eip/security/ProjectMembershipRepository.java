package io.eip.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID> {
    Optional<ProjectMembership> findByUserIdAndProjectId(UUID userId, UUID projectId);
}
