package com.guideaut.project.recomendacao.dto;

public record RecomendacaoRequest(
    String titulo,
    String descricao,
    String categoria,
    String referencia 
) {
}