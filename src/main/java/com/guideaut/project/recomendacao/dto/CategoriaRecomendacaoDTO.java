package com.guideaut.project.recomendacao.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRecomendacaoDTO {
    
    
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
}