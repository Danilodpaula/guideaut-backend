package com.guideaut.project.report;

import com.guideaut.project.report.dto.CreateReportRequest;
import com.guideaut.project.report.dto.ReportResponse;
import com.guideaut.project.report.dto.UpdateReportStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Denúncias (Reports)")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @Operation(summary = "Criar uma nova denúncia")
    @PostMapping("/reports")
    public ResponseEntity<Void> create(
            @RequestBody CreateReportRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        service.create(req, auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Listar todas as denúncias (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reports")
    public ResponseEntity<List<ReportResponse>> listAdmin() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Atualizar status da denúncia (ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/reports/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateReportStatusRequest req,
            Authentication auth,
            HttpServletRequest request
    ) {
        service.updateStatus(id, req.status(), auth.getName(), request);
        return ResponseEntity.ok().build();
    }
}