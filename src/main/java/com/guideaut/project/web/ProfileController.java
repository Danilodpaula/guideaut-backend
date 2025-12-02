package com.guideaut.project.web;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.files.AvatarService;
import com.guideaut.project.identity.Papel;
import com.guideaut.project.repo.UsuarioRepo;
import com.guideaut.project.web.dto.AvatarResponse;
import com.guideaut.project.web.dto.MeResponse;
import com.guideaut.project.web.dto.UpdateProfileRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Usuário")
public class ProfileController {

    private final UsuarioRepo users;
    private final AvatarService avatarService;
    private final AuditService auditService;

    public ProfileController(UsuarioRepo users,
                             AvatarService avatarService,
                             AuditService auditService) {
        this.users = users;
        this.avatarService = avatarService;
        this.auditService = auditService;
    }

    // =========================
    // GET /me
    // =========================
    @Operation(summary = "Dados do usuário autenticado")
    @GetMapping("/me")
    public MeResponse me(Authentication auth, HttpServletRequest request) {
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

        MeResponse response = new MeResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                rolesFromDb,
                avatarUrl,
                u.getDisplayName(),
                u.getBio()
        );

        // Audit (consulta de perfil)
        auditService.log(
                "PROFILE_VIEW",
                u.getEmail(),
                request,
                Map.of("userId", u.getId()),
                AuditSeverity.INFO
        );

        return response;
    }

    // =========================
    // PATCH /users/me
    // =========================
    @Operation(summary = "Atualizar perfil do usuário autenticado")
    @PatchMapping("/users/me")
    public MeResponse updateMyProfile(
            Authentication auth,
            @Valid @RequestBody UpdateProfileRequest body,
            HttpServletRequest request
    ) {
        var u = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (body.displayName() != null) {
            u.setDisplayName(body.displayName());
        }
        if (body.bio() != null) {
            u.setBio(body.bio());
        }
        users.save(u);

        String avatarUrl = (u.getAvatarPath() != null && !u.getAvatarPath().isBlank())
                ? "/files/" + u.getAvatarPath()
                : null;

        List<String> rolesFromDb = u.getPapeis().stream()
                .map(Papel::getNome)
                .toList();

        // Audit (perfil atualizado)
        auditService.log(
                "PROFILE_UPDATED",
                u.getEmail(),
                request,
                Map.of(
                        "userId", u.getId(),
                        "displayName", u.getDisplayName(),
                        "bioLength", u.getBio() != null ? u.getBio().length() : 0
                ),
                AuditSeverity.INFO
        );

        return new MeResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                rolesFromDb,
                avatarUrl,
                u.getDisplayName(),
                u.getBio()
        );
    }

    // =========================
    // POST /users/me/avatar
    // =========================
    @Operation(
        summary = "Enviar/atualizar avatar do usuário autenticado",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
    )
    @PostMapping(value = "/users/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AvatarResponse uploadMyAvatar(
            Authentication auth,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {

        var u = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String relativePath = avatarService.saveUserAvatar(u.getId(), file);

        if (u.getAvatarPath() != null && !u.getAvatarPath().equals(relativePath)) {
            avatarService.deleteUserAvatar(u.getAvatarPath());
        }

        u.setAvatarPath(relativePath);
        users.save(u);

        // Audit (avatar atualizado)
        auditService.log(
                "AVATAR_UPDATED",
                u.getEmail(),
                request,
                Map.of(
                        "userId", u.getId(),
                        "avatarPath", relativePath
                ),
                AuditSeverity.INFO
        );

        return new AvatarResponse("/files/" + relativePath);
    }

    // =========================
    // DELETE /users/me/avatar
    // =========================
    @Operation(summary = "Remover avatar do usuário autenticado")
    @DeleteMapping("/users/me/avatar")
    public ResponseEntity<Void> deleteMyAvatar(Authentication auth,
                                               HttpServletRequest request) {
        var u = users.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (u.getAvatarPath() != null) {
            String oldPath = u.getAvatarPath();
            avatarService.deleteUserAvatar(oldPath);
            u.setAvatarPath(null);
            users.save(u);

            // Audit (avatar removido)
            auditService.log(
                    "AVATAR_REMOVED",
                    u.getEmail(),
                    request,
                    Map.of(
                            "userId", u.getId(),
                            "oldAvatarPath", oldPath
                    ),
                    AuditSeverity.WARNING
            );
        }
        return ResponseEntity.noContent().build();
    }
}
