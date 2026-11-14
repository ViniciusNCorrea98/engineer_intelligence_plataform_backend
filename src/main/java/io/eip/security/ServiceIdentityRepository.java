package io.eip.security;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceIdentityRepository extends JpaRepository<ServiceIdentity, UUID> {

    Optional<ServiceIdentity> findByIdAndProjectId(UUID id, UUID projectId);
}
