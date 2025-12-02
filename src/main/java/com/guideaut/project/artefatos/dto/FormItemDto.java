package com.guideaut.project.artefatos.dto;

import com.guideaut.project.artefatos.model.Form;
import com.guideaut.project.artefatos.model.FormItem;

public record FormItemDto (
        String section,
        Boolean isFixed,
        String question
)
{
    public FormItem toEntity(Form form) {
        FormItem item = new FormItem();
        item.setSection(this.section);
        item.setIsFixed(this.isFixed);
        item.setQuestion(this.question);
        item.setForm(form);
        return item;
    }
}
