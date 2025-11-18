package com.guideaut.project.recomendacao.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ComentarioResponse(
    UUID id,
    String texto,
    String autorNome,
    String autorAvatar,
    OffsetDateTime criadoEm
) {}