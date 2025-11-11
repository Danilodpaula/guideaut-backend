// src/main/java/com/guideaut/project/recomendacao/RecomendacaoController.java
package com.guideaut.project.recomendacao;

import com.guideaut.project.recomendacao.dto.RecomendacaoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recomendacoes")
@Tag(name = "Recomendações")
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    public RecomendacaoController(RecomendacaoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    @Operation(summary = "Lista todas as recomendações")
    @GetMapping("/list-all")
    public ResponseEntity<List<Recomendacao>> listarRecomendacoes() {
        return ResponseEntity.ok(recomendacaoService.listarTodas());
    }

    @Operation(summary = "Cria uma nova recomendação")
    @PostMapping
    public ResponseEntity<Recomendacao> criarRecomendacao(
            @RequestBody RecomendacaoRequest request
    ) {
        Recomendacao criada = recomendacaoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(summary = "Atualiza uma recomendação existente")
    @PutMapping("/{id}")
    public ResponseEntity<Recomendacao> atualizarRecomendacao(
            @PathVariable UUID id,
            @RequestBody RecomendacaoRequest request
    ) {
        Recomendacao atualizada = recomendacaoService.atualizar(id, request);
        return ResponseEntity.ok(atualizada); 
    }

    @Operation(summary = "Deleta uma recomendação")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRecomendacao(
            @PathVariable UUID id
    ) {
        recomendacaoService.deletar(id);
        return ResponseEntity.noContent().build(); 
    }

} 