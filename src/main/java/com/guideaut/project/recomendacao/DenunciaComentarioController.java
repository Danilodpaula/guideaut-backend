package com.guideaut.project.recomendacao;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.recomendacao.dto.CreateDenunciaComentarioDto;
import com.guideaut.project.recomendacao.dto.DenunciaComentarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/denuncia-comentarios")
@Tag(name = "Denúncias de Comentários")
public class DenunciaComentarioController {

    private final DenunciaComentarioService denunciaService;
    private final AuditService auditService;

    public DenunciaComentarioController(DenunciaComentarioService denunciaService,
                                       AuditService auditService) {
        this.denunciaService = denunciaService;
        this.auditService = auditService;
    }

    @Operation(summary = "Lista todas as denúncias")
    @GetMapping("/list-all")
    public ResponseEntity<List<DenunciaComentarioResponse>> listarDenuncias(
            Authentication authentication,
            HttpServletRequest request
    ) {
        List<DenunciaComentarioResponse> todas = denunciaService.listarTodasResponse();

        auditService.log(
                "DENUNCIA_COMENTARIO_LIST_ALL",
                authentication != null ? authentication.getName() : "ANONYMOUS",
                request,
                Map.of("quantidade", todas.size()),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(todas);
    }

    @Operation(summary = "Lista denúncias por status")
    @GetMapping("/por-status/{status}")
    public ResponseEntity<List<DenunciaComentarioResponse>> listarPorStatus(
            @PathVariable String status,
            Authentication authentication,
            HttpServletRequest request
    ) {
        List<DenunciaComentarioResponse> denuncias = denunciaService.listarPorStatusResponse(status);

        auditService.log(
                "DENUNCIA_COMENTARIO_LIST_BY_STATUS",
                authentication != null ? authentication.getName() : "ANONYMOUS",
                request,
                Map.of("status", status, "quantidade", denuncias.size()),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(denuncias);
    }

    @Operation(summary = "Lista denúncias de um comentário específico")
    @GetMapping("/comentario/{comentarioId}")
    public ResponseEntity<List<DenunciaComentarioResponse>> listarPorComentario(
            @PathVariable UUID comentarioId,
            Authentication authentication,
            HttpServletRequest request
    ) {
        List<DenunciaComentarioResponse> denuncias = denunciaService.listarPorComentarioResponse(comentarioId);

        auditService.log(
                "DENUNCIA_COMENTARIO_LIST_BY_COMENTARIO",
                authentication != null ? authentication.getName() : "ANONYMOUS",
                request,
                Map.of("comentarioId", comentarioId, "quantidade", denuncias.size()),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(denuncias);
    }

    @Operation(summary = "Obtém uma denúncia específica")
    @GetMapping("/{id}")
    public ResponseEntity<DenunciaComentarioResponse> obterDenuncia(
            @PathVariable UUID id,
            Authentication authentication,
            HttpServletRequest request
    ) {
        DenunciaComentarioResponse denuncia = denunciaService.obterResponse(id);

        auditService.log(
                "DENUNCIA_COMENTARIO_GET",
                authentication != null ? authentication.getName() : "ANONYMOUS",
                request,
                Map.of("denunciaId", id),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(denuncia);
    }

    @Operation(summary = "Cria uma nova denúncia de comentário")
    @PostMapping("/comentario/{comentarioId}")
    public ResponseEntity<DenunciaComentarioResponse> criarDenuncia(
            @PathVariable UUID comentarioId,
            @RequestBody CreateDenunciaComentarioDto requestBody,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String email = authentication.getName();
        DenunciaComentario criada = denunciaService.criar(comentarioId, requestBody, email);

        auditService.log(
                "DENUNCIA_COMENTARIO_CREATED",
                email,
                request,
                Map.of(
                        "denunciaId", criada.getId(),
                        "comentarioId", comentarioId,
                        "payload", requestBody
                ),
                AuditSeverity.WARNING
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(denunciaService.obterResponse(criada.getId()));
    }

    @Operation(summary = "Atualiza o status de uma denúncia")
    @PatchMapping("/{id}/status/{novoStatus}")
    public ResponseEntity<DenunciaComentarioResponse> atualizarStatus(
            @PathVariable UUID id,
            @PathVariable String novoStatus,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String email = authentication.getName();
        DenunciaComentario atualizada = denunciaService.atualizarStatus(id, novoStatus, email);

        auditService.log(
                "DENUNCIA_COMENTARIO_STATUS_UPDATED",
                email,
                request,
                Map.of(
                        "denunciaId", id,
                        "novoStatus", novoStatus
                ),
                AuditSeverity.WARNING
        );

        return ResponseEntity.ok(denunciaService.obterResponse(atualizada.getId()));
    }

    @Operation(summary = "Deleta uma denúncia")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDenuncia(
            @PathVariable UUID id,
            Authentication authentication,
            HttpServletRequest request
    ) {
        denunciaService.deletar(id);

        auditService.log(
                "DENUNCIA_COMENTARIO_DELETED",
                authentication != null ? authentication.getName() : "SYSTEM",
                request,
                Map.of("denunciaId", id),
                AuditSeverity.WARNING
        );

        return ResponseEntity.noContent().build();
    }
}
