package com.guideaut.project.web.dto;

import com.guideaut.project.identity.UserStatus;
import jakarta.validation.constraints.NotNull;

public record AdminUpdateStatusRequest(
        @NotNull UserStatus status) {
}
