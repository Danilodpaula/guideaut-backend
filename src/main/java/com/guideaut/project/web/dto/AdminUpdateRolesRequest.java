package com.guideaut.project.web.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdminUpdateRolesRequest(
        @NotEmpty List<String> roles // nomes dos papeis, ex: ["ADMIN", "USER"]
) {
}
