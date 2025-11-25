package com.guideaut.project.artefatos.dto;

import com.guideaut.project.artefatos.model.Form;
import java.util.List;

public record CreateFormDto (
        String name,
        String type,
        List<FormItemDto> items
)
{
    public Form toEntity() {
        Form form = new Form();
        form.setName(this.name);
        form.setType(this.type);
        form.setItems(this.items.stream().map((i) -> i.toEntity(form)).toList());
        return form;
    }
}