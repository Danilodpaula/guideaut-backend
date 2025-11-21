package com.guideaut.project.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateProfileRequest(
        @JsonProperty("display_name") String displayName,
        String bio
) {
}
