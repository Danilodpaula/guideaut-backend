package com.guideaut.project.artefatos.dto;

import java.util.List;

public record UpdateEmpathyDto (
        String name,
        Integer age,
        String gender,
        String reasons,
        String expectations,
        List<String> interaction,
        List<String> cognition,
        List<String> communication,
        List<String> behavior
) {}
