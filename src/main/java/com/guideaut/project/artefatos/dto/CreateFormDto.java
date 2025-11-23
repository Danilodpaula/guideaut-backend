package com.guideaut.project.artefatos.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateFormDto {
    private String name;
    private String type;
    private List<FormItemDto> items = new ArrayList<>();
}
