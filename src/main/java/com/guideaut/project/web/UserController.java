package com.guideaut.project.web;

import com.guideaut.project.identity.Papel;
import com.guideaut.project.identity.Usuario;
import com.guideaut.project.identity.UserStatus;
import com.guideaut.project.repo.PapelRepo;
import com.guideaut.project.repo.UsuarioRepo;
import com.guideaut.project.web.dto.CreateUserRequest;
import com.guideaut.project.web.dto.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springdoc.core.annotations.ParameterObject;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Tag(name = "user-controller") // organiza no Swagger
public class UserController {

    private final UsuarioRepo users;
    private final PapelRepo roles;
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder;

    public UserController(
            UsuarioRepo users,
            PapelRepo roles,
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder
    ) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    // IMPORTANTE: o endpoint /me foi movido para ProfileController
    // para incluir avatarUrl. Não deixe outro /me aqui para evitar conflito.

    // ------------------------------------------------------------
    // /admin/users - Listar usuários (ADMIN), paginado
    // ------------------------------------------------------------
    @Operation(
        summary = "Listar usuários (ADMIN)",
        description = "Retorna a lista paginada de usuários. Requer papel ADMIN.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @PreAuthorize("hasRole('ADMIN')") // Requer ROLE_ADMIN
    @GetMapping("/admin/users")
    public ResponseEntity<Page<UserResponse>> listUsers(@ParameterObject Pageable pageable) {
        Page<UserResponse> page = users.findAll(pageable)
            .map(u -> new UserResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPapeis().stream().map(Papel::getNome).toList(),
                u.getStatus().name()
            ));

        return ResponseEntity.ok(page);
    }

    // ------------------------------------------------------------
    // /users - Criar usuário (público)
    // ------------------------------------------------------------
    @Operation(summary = "Criar usuário (público)")
    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest req) {
        if (users.findByEmail(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        // Garante papel USER
        var userRole = roles.findByNome("USER").orElseGet(() -> {
            var r = new Papel();
            r.setNome("USER");
            return roles.save(r);
        });

        var u = new Usuario();
        u.setNome(req.nome());
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setStatus(UserStatus.PENDING); // ou ACTIVE se quiser liberar acesso imediato
        u.getPapeis().add(userRole);

        u = users.save(u);

        var resp = new UserResponse(
            u.getId(),
            u.getNome(),
            u.getEmail(),
            u.getPapeis().stream().map(Papel::getNome).toList(),
            u.getStatus().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
