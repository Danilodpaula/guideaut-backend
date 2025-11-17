package com.guideaut.project.audit;

import com.guideaut.project.repo.AuditLogRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping("/audit")
@Tag(name = "audit-controller")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditLogRepo repo;

    public AuditController(AuditLogRepo repo) {
        this.repo = repo;
    }

    @Operation(summary = "Listar logs de auditoria")
    @GetMapping("/logs")
    public List<AuditLogResponse> list(
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String event,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        OffsetDateTime start = null;
        OffsetDateTime end = null;

        if (startDate != null) {
            start = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        }
        if (endDate != null) {
            // inclui o dia inteiro
            end = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        }

        return repo.search(userEmail, event, start, end)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }
}
