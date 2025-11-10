package com.guideaut.project.recomendacao;

import com.guideaut.project.recomendacao.dto.RecomendacaoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}