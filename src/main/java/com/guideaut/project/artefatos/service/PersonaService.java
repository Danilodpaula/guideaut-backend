package com.guideaut.project.artefatos.service;

import com.guideaut.project.artefatos.dto.CreatePersonaDto;
import com.guideaut.project.artefatos.dto.FindPersonaDto;
import com.guideaut.project.artefatos.dto.UpdatePersonaDto;
import com.guideaut.project.artefatos.model.Persona;
import com.guideaut.project.artefatos.repository.PersonaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PersonaService {

    @NonNull
    private PersonaRepository repository;

    @Transactional(readOnly = true)
    public FindPersonaDto findOne(UUID id) {
        Persona persona = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona não encontrada!"));
        return persona.toDto();
    }

    @Transactional(readOnly = true)
    public List<FindPersonaDto> findAll() {
        return repository.findAll().stream().map(Persona::toDto).toList();
    }

    public FindPersonaDto create(CreatePersonaDto dto) {
        Persona persona = repository.save(dto.toEntity());
        return persona.toDto();
    }

    public FindPersonaDto update(UUID id, UpdatePersonaDto dto) {
        Persona persona = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona não encontrada!"));
        persona.update(dto);
        persona = repository.save(persona);
        return persona.toDto();
    }

    public void remove(UUID id) {
        repository.deleteById(id);
    }
}
