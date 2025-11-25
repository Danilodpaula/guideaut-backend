package com.guideaut.project.artefatos.dto;

import java.util.List;
import java.util.UUID;

public record FindEmpathyDto (
        UUID id,
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
