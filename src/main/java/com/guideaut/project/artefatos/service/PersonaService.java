package com.guideaut.project.artefatos.service;

import com.guideaut.project.artefatos.dto.CreatePersonaDto;
import com.guideaut.project.artefatos.dto.FindPersonaDto;
import com.guideaut.project.artefatos.dto.UpdatePersonaDto;
import com.guideaut.project.artefatos.model.Persona;
import com.guideaut.project.artefatos.repository.PersonaRepository;
import com.guideaut.project.identity.Usuario;
import com.guideaut.project.repo.UsuarioRepo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PersonaService {

    @NonNull
    private PersonaRepository repository;

    @NonNull
    private UsuarioRepo usuarioRepo;

    private Usuario findUsuario(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    @Transactional(readOnly = true)
    public FindPersonaDto findOne(UUID id, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Persona persona = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona não encontrada!"));
        if (!persona.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse persona não pertence ao usuário!");
        }
        return persona.toDto();
    }

    @Transactional(readOnly = true)
    public List<FindPersonaDto> findAll(Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        return repository.findAllByUsuario(usuario).stream().map(Persona::toDto).toList();
    }

    public FindPersonaDto create(CreatePersonaDto dto, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Persona newPersona = dto.toEntity();
        newPersona.setUsuario(usuario);
        repository.save(newPersona);
        return newPersona.toDto();
    }

    public FindPersonaDto update(UUID id, UpdatePersonaDto dto, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Persona persona = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona não encontrada!"));
        if (!persona.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse persona não pertence ao usuário!");
        }
        persona.update(dto);
        persona = repository.save(persona);
        return persona.toDto();
    }

    public void remove(UUID id, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Persona persona = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona não encontrada!"));
        if (!persona.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse mapa de empatia não pertence ao usuário!");
        }
        repository.delete(persona);
    }
}
