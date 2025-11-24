package com.guideaut.project.artefatos.service;

import com.guideaut.project.artefatos.dto.CreateFormDto;
import com.guideaut.project.artefatos.dto.FindFormDto;
import com.guideaut.project.artefatos.dto.UpdateFormDto;
import com.guideaut.project.artefatos.model.Form;
import com.guideaut.project.artefatos.repository.FormRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FormService {

    @NonNull
    private FormRepository repository;

    public FindFormDto findOne(UUID id) {
        Form form = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado!"));
        return form.toDto();
    }

    public List<FindFormDto> findAll() {
        return repository.findAll().stream().map(Form::toDto).toList();
    }

    public FindFormDto create(CreateFormDto dto) {
        Form form = repository.save(dto.toEntity());
        return form.toDto();
    }

    public FindFormDto update(UUID id, UpdateFormDto dto) {
        Form form = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado!"));
        form.update(dto);
        form = repository.save(form);
        return form.toDto();
    }

    public void remove(UUID id) {
        repository.deleteById(id);
    }
}
