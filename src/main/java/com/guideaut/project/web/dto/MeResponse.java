package com.guideaut.project.web.dto;

import java.util.List;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String name,
        String email,
        List<String> roles,
        String avatarUrl) {
}
