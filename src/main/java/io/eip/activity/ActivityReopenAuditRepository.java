package io.eip.activity;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityReopenAuditRepository extends JpaRepository<ActivityReopenAudit, UUID> {
}
