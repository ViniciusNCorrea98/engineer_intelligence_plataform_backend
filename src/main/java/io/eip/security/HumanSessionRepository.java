package io.eip.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HumanSessionRepository extends JpaRepository<HumanSession, UUID> {
    Optional<HumanSession> findById(UUID id);
}
