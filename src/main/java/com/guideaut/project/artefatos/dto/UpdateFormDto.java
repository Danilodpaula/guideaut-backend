package com.guideaut.project.artefatos.dto;

import java.util.List;

public record UpdateFormDto (
        String name,
        String type,
        List<FormItemDto> items
) {}