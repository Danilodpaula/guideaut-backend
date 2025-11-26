package com.guideaut.project.recomendacao.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DenunciaComentarioResponse(
    UUID id,
    UUID comentarioId,
    String comentarioTexto,
    String autorComentario,
    String autorComentarioAvatar,
    String motivo,
    String descricao,
    String status,
    String denunciador,
    OffsetDateTime criadoEm,
    OffsetDateTime atualizadoEm
) {}
