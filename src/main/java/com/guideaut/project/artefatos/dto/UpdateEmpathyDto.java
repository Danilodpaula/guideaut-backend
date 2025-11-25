package com.guideaut.project.artefatos.dto;

import java.util.List;

public record UpdateEmpathyDto (
        String name,
        Integer age,
        String gender,
        String reasons,
        String expectations,
        List<String> interactionItems,
        List<String> cognitionItems,
        List<String> communicationItems,
        List<String> behaviorItems
) {}
