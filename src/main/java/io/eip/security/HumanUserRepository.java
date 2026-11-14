package io.eip.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HumanUserRepository extends JpaRepository<HumanUser, UUID> {
    Optional<HumanUser> findByEmail(String email);
}
