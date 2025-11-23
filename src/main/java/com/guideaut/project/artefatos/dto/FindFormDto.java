package com.guideaut.project.artefatos.dto;

import java.util.List;
import java.util.UUID;

public record FindFormDto (
        UUID id,
        String name,
        String type,
        List<FormItemDto> items
) {}
