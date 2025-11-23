package com.guideaut.project.artefatos.dto;

import java.util.List;
import java.util.UUID;

public record UpdateFormDto (
        UUID id,
        String name,
        String type,
        List<FormItemDto> items
) {}