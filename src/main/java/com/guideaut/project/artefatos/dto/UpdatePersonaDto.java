package com.guideaut.project.artefatos.dto;

import java.util.List;
import java.util.UUID;

public record UpdatePersonaDto (
        UUID id,
        String name,
        Integer age,
        String gender,
        String language,
        String supportLevel,
        String model,
        String about,
        List<String> interaction,
        List<String> cognition,
        List<String> communication,
        List<String> behavior,
        List<String> stressfulActivities,
        List<String> calmingActivities,
        List<String> stereotypes,
        List<String> softwareAspects,
        List<String> socialAspects
) {}
