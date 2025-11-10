package com.guideaut.project.web;

import com.guideaut.project.files.AvatarService;
import com.guideaut.project.identity.Papel;
import com.guideaut.project.repo.UsuarioRepo;
import com.guideaut.project.web.dto.AvatarResponse;
import com.guideaut.project.web.dto.MeResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
// IMPORTANTE: usar o RequestBody do OpenAPI aqui (qualificado para evitar conflito)
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@Tag(name = "user-controller")
public class ProfileController {

    private final UsuarioRepo users;
    private final AvatarService avatarService;

    public ProfileController(UsuarioRepo users, AvatarService avatarService) {
        this.users = users;
        this.avatarService = avatarService;
    }

    @Operation(summary = "Dados do usuário autenticado")
    @GetMapping("/me")
    public MeResponse me(Authentication auth) {
        var u = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<String> rolesFromDb = u.getPapeis().stream()
                .map(Papel::getNome)
                .toList();

        if (rolesFromDb.isEmpty() && auth.getAuthorities() != null) {
            rolesFromDb = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // ex.: ROLE_ADMIN
                    .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                    .toList();
        }

        String avatarUrl = (u.getAvatarPath() != null && !u.getAvatarPath().isBlank())
                ? "/files/" + u.getAvatarPath()
                : null;

        return new MeResponse(u.getId(), u.getNome(), u.getEmail(), rolesFromDb, avatarUrl);
    }

    @Operation(
        summary = "Enviar/atualizar avatar do usuário autenticado",
        // use o RequestBody do OpenAPI (sem schema para evitar conflitos; springdoc infere do MultipartFile)
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
    )
    @PostMapping(value = "/users/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AvatarResponse uploadMyAvatar(
            Authentication auth,
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        var u = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String relativePath = avatarService.saveUserAvatar(u.getId(), file);

        if (u.getAvatarPath() != null && !u.getAvatarPath().equals(relativePath)) {
            avatarService.deleteUserAvatar(u.getAvatarPath());
        }

        u.setAvatarPath(relativePath);
        users.save(u);

        return new AvatarResponse("/files/" + relativePath);
    }

    @Operation(summary = "Remover avatar do usuário autenticado")
    @DeleteMapping("/users/me/avatar")
    public ResponseEntity<Void> deleteMyAvatar(Authentication auth) {
        var u = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (u.getAvatarPath() != null) {
            avatarService.deleteUserAvatar(u.getAvatarPath());
            u.setAvatarPath(null);
            users.save(u);
        }
        return ResponseEntity.noContent().build();
    }
}
