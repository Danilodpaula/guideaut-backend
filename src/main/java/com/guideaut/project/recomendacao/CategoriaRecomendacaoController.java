package com.guideaut.project.recomendacao;

import com.guideaut.project.recomendacao.dto.CategoriaRecomendacaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categorias-recomendacao")
@AllArgsConstructor
@Tag(
    name = "Categorias de Recomendacao",
    description = "Endpoints para gerenciar categorias de recomendacoes"
)
@SecurityRequirement(name = "bearer-jwt")
public class CategoriaRecomendacaoController {

    private final CategoriaRecomendacaoService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Criar nova categoria de recomendacao",
        description = "Apenas administradores podem criar categorias"
    )
    public ResponseEntity<CategoriaRecomendacaoDTO> criar(@RequestBody CategoriaRecomendacaoDTO dto) {
        CategoriaRecomendacaoDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    // Papeis sao gravados como strings exatas (ex.: ADMIN), entao usamos hasAnyAuthority em vez de hasAnyRole.
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Listar todas as categorias", description = "Usuarios autenticados podem listar categorias")
    public ResponseEntity<List<CategoriaRecomendacaoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Obter categoria por ID")
    public ResponseEntity<CategoriaRecomendacaoDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obterPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Atualizar categoria", description = "Apenas administradores podem atualizar")
    public ResponseEntity<CategoriaRecomendacaoDTO> atualizar(
        @PathVariable Long id,
        @RequestBody CategoriaRecomendacaoDTO dto
    ) {
        CategoriaRecomendacaoDTO atualizada = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Deletar categoria", description = "Apenas administradores podem deletar")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
