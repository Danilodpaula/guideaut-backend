package com.guideaut.project.recomendacao;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.recomendacao.dto.AvaliacaoRequest;
import com.guideaut.project.recomendacao.dto.RecomendacaoRequest;
import com.guideaut.project.recomendacao.dto.ComentarioRequest; 
import com.guideaut.project.recomendacao.dto.ComentarioResponse;
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
@RequestMapping("/recomendacoes")
@Tag(name = "Recomendações")
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;
    private final AuditService auditService;

    public RecomendacaoController(RecomendacaoService recomendacaoService,
                                  AuditService auditService) {
        this.recomendacaoService = recomendacaoService;
        this.auditService = auditService;
    }

    @Operation(summary = "Lista todas as recomendações")
    @GetMapping("/list-all")
    public ResponseEntity<List<Recomendacao>> listarRecomendacoes(
            Authentication authentication,
            HttpServletRequest request
    ) {
        List<Recomendacao> todas = recomendacaoService.listarTodas();

        auditService.log(
                "RECOMENDACAO_LIST_ALL",
                authentication != null ? authentication.getName() : "ANONYMOUS",
                request,
                Map.of("quantidade", todas.size()),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(todas);
    }

    @Operation(summary = "Cria uma nova recomendação")
    @PostMapping
    public ResponseEntity<Recomendacao> criarRecomendacao(
            @RequestBody RecomendacaoRequest requestBody,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Recomendacao criada = recomendacaoService.criar(requestBody);

        auditService.log(
                "RECOMENDACAO_CREATED",
                authentication != null ? authentication.getName() : "SYSTEM",
                request,
                Map.of(
                        "recomendacaoId", criada.getId(),
                        "payload", requestBody
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(summary = "Atualiza uma recomendação existente")
    @PutMapping("/{id}")
    public ResponseEntity<Recomendacao> atualizarRecomendacao(
            @PathVariable UUID id,
            @RequestBody RecomendacaoRequest requestBody,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Recomendacao atualizada = recomendacaoService.atualizar(id, requestBody);

        auditService.log(
                "RECOMENDACAO_UPDATED",
                authentication != null ? authentication.getName() : "SYSTEM",
                request,
                Map.of(
                        "recomendacaoId", id,
                        "payload", requestBody
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(atualizada);
    }

    @Operation(summary = "Deleta uma recomendação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRecomendacao(
            @PathVariable UUID id,
            Authentication authentication,
            HttpServletRequest request
    ) {
        recomendacaoService.deletar(id);

        auditService.log(
                "RECOMENDACAO_DELETED",
                authentication != null ? authentication.getName() : "SYSTEM",
                request,
                Map.of("recomendacaoId", id),
                AuditSeverity.WARNING
        );

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adiciona ou atualiza uma avaliação (1-5 estrelas)")
    @PostMapping("/{id}/avaliar")
    public ResponseEntity<Recomendacao> avaliarRecomendacao(
            @PathVariable UUID id,
            @RequestBody AvaliacaoRequest requestBody,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String email = authentication.getName();
        Recomendacao atualizada = recomendacaoService.avaliar(id, requestBody, email);

        auditService.log(
                "RECOMENDACAO_AVALIADA",
                email,
                request,
                Map.of(
                        "recomendacaoId", id,
                        "payload", requestBody
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(atualizada);
    }

    @Operation(summary = "Lista comentários de uma recomendação")
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioResponse>> listarComentarios(@PathVariable UUID id) {
        List<ComentarioResponse> response = recomendacaoService.listarComentariosResponse(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Adiciona um comentário a uma recomendação")
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioResponse> adicionarComentario(
            @PathVariable UUID id,
            @RequestBody ComentarioRequest requestBody,
            Authentication authentication,
            HttpServletRequest request
    ) {
        String email = authentication.getName();
        RecomendacaoComentario salvo = recomendacaoService.comentar(id, requestBody.texto(), email);

        auditService.log(
                "RECOMENDACAO_COMENTADA",
                email,
                request,
                Map.of("recomendacaoId", id),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(new ComentarioResponse(
            salvo.getId(),
            salvo.getTexto(),
            salvo.getUsuario().getNome(),
            salvo.getUsuario().getAvatarPath() != null ? "/files/" + salvo.getUsuario().getAvatarPath() : null,
            salvo.getCriadoEm()
        ));
    }
}