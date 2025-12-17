package com.guideaut.project.artefatos.service;

import com.guideaut.project.artefatos.dto.CreateFormDto;
import com.guideaut.project.artefatos.dto.FindFormDto;
import com.guideaut.project.artefatos.dto.UpdateFormDto;
import com.guideaut.project.artefatos.model.Form;
import com.guideaut.project.artefatos.repository.FormRepository;
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
public class FormService {

    @NonNull
    private FormRepository repository;

    @NonNull
    private UsuarioRepo usuarioRepo;

    private Usuario findUsuario(String email) {
        return usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
    }

    @Transactional(readOnly = true)
    public FindFormDto findOne(UUID id, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Form form = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado!"));
        if (!form.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse formulário não pertence ao usuário!");
        }
        return form.toDto();
    }

    @Transactional(readOnly = true)
    public List<FindFormDto> findAll(Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        return repository.findAllByUsuario(usuario).stream().map(Form::toDto).toList();
    }

    public FindFormDto create(CreateFormDto dto, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Form newForm = dto.toEntity();
        newForm.setUsuario(usuario);
        repository.save(newForm);
        return newForm.toDto();
    }

    public FindFormDto update(UUID id, UpdateFormDto dto, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Form form = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado!"));
        if (!form.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse formulário não pertence ao usuário!");
        }
        form.update(dto);
        form = repository.save(form);
        return form.toDto();
    }

    public void remove(UUID id, Authentication auth) {
        Usuario usuario = findUsuario(auth.getName());
        Form form = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado!"));
        if (!form.getUsuario().getEmail().equals(usuario.getEmail())) {
            throw new AccessDeniedException("Esse formulario não pertence ao usuário!");
        }
        repository.delete(form);
    }
}
