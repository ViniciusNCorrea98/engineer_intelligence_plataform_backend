package io.eip.security;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSourceRepository extends JpaRepository<ProjectSource, UUID> {

    Optional<ProjectSource> findByProjectIdAndSourceKey(UUID projectId, String sourceKey);
}
