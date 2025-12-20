package com.guideaut.project.repo;

import com.guideaut.project.audit.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepo extends JpaRepository<AuditLog, UUID> {

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:email IS NULL OR a.usuarioEmail = :email)
          AND (:event IS NULL OR a.evento = :event)
          AND (cast(:start as timestamp) IS NULL OR a.timestamp >= :start)
          AND (cast(:end as timestamp) IS NULL OR a.timestamp <= :end)
        ORDER BY a.timestamp DESC
        """)
    List<AuditLog> search(
            @Param("email") String email,
            @Param("event") String event,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );
}