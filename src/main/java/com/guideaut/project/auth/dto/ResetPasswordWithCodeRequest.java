package com.guideaut.project.auth.dto;

public record ResetPasswordWithCodeRequest(
        String email,
        String code,
        String newPassword
) {}
