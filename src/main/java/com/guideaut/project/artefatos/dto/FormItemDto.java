package com.guideaut.project.artefatos.dto;

import com.guideaut.project.artefatos.model.FormItem;

public record FormItemDto (
        String section,
        String question
)
{
    public FormItem toEntity() {
        FormItem item = new FormItem();
        item.setSection(this.section);
        item.setQuestion(this.question);
        return item;
    }
}
