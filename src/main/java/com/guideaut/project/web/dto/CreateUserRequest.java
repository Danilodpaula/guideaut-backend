package com.guideaut.project.web.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank @Size(min = 3, max = 120) String nome,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6, max = 100) String password
) {}
