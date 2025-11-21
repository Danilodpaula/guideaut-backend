package com.guideaut.project.web.dto;

import java.util.List;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String nome,
    String email,
    List<String> roles,
    String status
) {}
