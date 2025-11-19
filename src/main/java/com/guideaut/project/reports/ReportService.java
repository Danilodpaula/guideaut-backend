package com.guideaut.project.report;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.identity.Usuario;
import com.guideaut.project.recomendacao.Recomendacao;
import com.guideaut.project.repo.RecomendacaoComentarioRepo;
import com.guideaut.project.repo.RecomendacaoRepo;
import com.guideaut.project.repo.ReportRepo;
import com.guideaut.project.repo.UsuarioRepo;
import com.guideaut.project.report.dto.CreateReportRequest;
import com.guideaut.project.report.dto.ReportResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepo reportRepo;
    private final UsuarioRepo usuarioRepo;
    private final RecomendacaoRepo recomendacaoRepo;
    private final RecomendacaoComentarioRepo comentarioRepo;
    private final AuditService auditService;

    public ReportService(ReportRepo reportRepo, UsuarioRepo usuarioRepo, RecomendacaoRepo recomendacaoRepo, RecomendacaoComentarioRepo comentarioRepo, AuditService auditService) {
        this.reportRepo = reportRepo;
        this.usuarioRepo = usuarioRepo;
        this.recomendacaoRepo = recomendacaoRepo;
        this.comentarioRepo = comentarioRepo;
        this.auditService = auditService;
    }

    public void create(CreateReportRequest req, String reporterEmail, HttpServletRequest request) {
        Usuario reporter = usuarioRepo.findByEmail(reporterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Report report = new Report();
        report.setReporter(reporter);
        report.setTargetId(req.targetId());
        report.setType(req.type());
        report.setReason(req.reason());
        report.setDescription(req.description());

        reportRepo.save(report);

        auditService.log(
                "REPORT_CREATED",
                reporterEmail,
                request,
                Map.of("type", req.type(), "targetId", req.targetId()),
                AuditSeverity.WARNING
        );
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> findAll() {
        return reportRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void updateStatus(UUID id, ReportStatus newStatus, String adminEmail, HttpServletRequest request) {
        Report report = reportRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        ReportStatus oldStatus = report.getStatus();
        report.setStatus(newStatus);
        reportRepo.save(report);

        auditService.log(
                "REPORT_STATUS_UPDATED",
                adminEmail,
                request,
                Map.of(
                        "reportId", id,
                        "oldStatus", oldStatus,
                        "newStatus", newStatus
                ),
                AuditSeverity.INFO
        );
    }

    private ReportResponse mapToResponse(Report r) {
        String targetName = "Desconhecido/Removido";

        try {
            if (r.getType() == ReportType.RECOMMENDATION) {
                targetName = recomendacaoRepo.findById(r.getTargetId())
                        .map(Recomendacao::getTitulo)
                        .orElse("Recomendação Removida");
            } else if (r.getType() == ReportType.USER) {
                targetName = usuarioRepo.findById(r.getTargetId())
                        .map(Usuario::getNome)
                        .orElse("Usuário Removido");
            } else if (r.getType() == ReportType.COMMENT) {
                targetName = comentarioRepo.findById(r.getTargetId())
                        .map(c -> "Comentário: " + (c.getTexto().length() > 20 ? c.getTexto().substring(0, 20) + "..." : c.getTexto()))
                        .orElse("Comentário Removido");
            }
        } catch (Exception e) {
        }

        return new ReportResponse(
                r.getId(),
                r.getTargetId(),
                targetName,
                r.getType(),
                r.getReporter().getNome(),
                r.getReason(),
                r.getDescription(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}