package com.guideaut.project.repo;

import com.guideaut.project.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogRepo extends JpaRepository<AuditLog, UUID> {
}
