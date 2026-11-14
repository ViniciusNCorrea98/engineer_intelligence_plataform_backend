package io.eip.security;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceGrantRepository extends JpaRepository<ServiceGrant, UUID> {

    boolean existsByServiceIdentityIdAndProjectSourceIdAndScope(UUID identityId, UUID sourceId, ServiceScope scope);
}
