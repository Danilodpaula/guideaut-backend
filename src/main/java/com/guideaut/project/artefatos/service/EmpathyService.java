package com.guideaut.project.artefatos.service;

import com.guideaut.project.artefatos.dto.CreateEmpathyDto;
import com.guideaut.project.artefatos.dto.FindEmpathyDto;
import com.guideaut.project.artefatos.dto.UpdateEmpathyDto;
import com.guideaut.project.artefatos.model.Empathy;
import com.guideaut.project.artefatos.repository.EmpathyRepository;
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
public class EmpathyService {

    @NonNull
    private EmpathyRepository repository;

    @NonNull
    private UsuarioRepo usuarioRepo;

    private Usuario findUsuario(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    @Transactional(readOnly = true)
    public FindEmpathyDto findOne(UUID id, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Empathy empathy = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empatia não encontrada!"));
        if (!empathy.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse mapa de empatia não pertence ao usuário!");
        }
        return empathy.toDto();
    }

    @Transactional(readOnly = true)
    public List<FindEmpathyDto> findAll(Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        return repository.findAllByUsuario(usuario).stream().map(Empathy::toDto).toList();
    }

    public FindEmpathyDto create(CreateEmpathyDto dto, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Empathy newEmpathy = dto.toEntity();
        newEmpathy.setUsuario(usuario);
        repository.save(newEmpathy);
        return newEmpathy.toDto();
    }

    public FindEmpathyDto update(UUID id, UpdateEmpathyDto dto, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Empathy empathy = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empatia não encontrada!"));
        if (!empathy.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse mapa de empatia não pertence ao usuário!");
        }
        empathy.update(dto);
        empathy = repository.save(empathy);
        return empathy.toDto();
    }

    public void remove(UUID id, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Empathy empathy = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empatia não encontrada!"));
        if (!empathy.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse mapa de empatia não pertence ao usuário!");
        }
        repository.delete(empathy);
    }
}
