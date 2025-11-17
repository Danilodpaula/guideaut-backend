package com.guideaut.project.repo;

import com.guideaut.project.audit.AuditLog;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepo extends JpaRepository<AuditLog, UUID> {

    @Query("""
        select a from AuditLog a
        where (:email is null or a.usuarioEmail = :email)
          and (:event is null or a.evento = :event)
          and (:start is null or a.timestamp >= :start)
          and (:end is null or a.timestamp <= :end)
        order by a.timestamp desc
        """)
    List<AuditLog> search(
            @Param("email") String email,
            @Param("event") String event,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );
}
