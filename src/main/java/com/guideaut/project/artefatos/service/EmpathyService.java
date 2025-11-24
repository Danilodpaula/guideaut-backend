package com.guideaut.project.artefatos.service;

import com.guideaut.project.artefatos.dto.CreateEmpathyDto;
import com.guideaut.project.artefatos.dto.FindEmpathyDto;
import com.guideaut.project.artefatos.dto.UpdateEmpathyDto;
import com.guideaut.project.artefatos.model.Empathy;
import com.guideaut.project.artefatos.repository.EmpathyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EmpathyService {

    @NonNull
    private EmpathyRepository repository;

    public FindEmpathyDto findOne(UUID id) {
        Empathy empathy = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empatia não encontrada!"));
        return empathy.toDto();
    }

    public List<FindEmpathyDto> findAll() {
        return repository.findAll().stream().map(Empathy::toDto).toList();
    }

    public FindEmpathyDto create(CreateEmpathyDto dto) {
        Empathy empathy = repository.save(dto.toEntity());
        return empathy.toDto();
    }

    public FindEmpathyDto update(UUID id, UpdateEmpathyDto dto) {
        Empathy empathy = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empatia não encontrada!"));
        empathy.update(dto);
        empathy = repository.save(empathy);
        return empathy.toDto();
    }

    public void remove(UUID id) {
        repository.deleteById(id);
    }
}
