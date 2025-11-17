package com.guideaut.project.audit;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String event,
        String user,
        String ip,
        String userAgent,
        String details,
        OffsetDateTime timestamp,
        String severity
) {
    public static AuditLogResponse fromEntity(AuditLog a) {
        return new AuditLogResponse(
                a.getId(),
                a.getEvento(),
                a.getUsuarioEmail(),
                a.getIp(),
                a.getUserAgent(),
                a.getDetalhesJson(),
                a.getTimestamp(),
                a.getSeverity() != null ? a.getSeverity().name().toLowerCase() : "info"
        );
    }
}
