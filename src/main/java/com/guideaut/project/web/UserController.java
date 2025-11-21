package com.guideaut.project.web;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@Tag(name = "user-controller") // organiza no Swagger
public class UserController {

    private final UsuarioRepo users;
    private final PapelRepo roles;
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder;
    private final AuditService auditService;

    public UserController(
            UsuarioRepo users,
            PapelRepo roles,
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder,
            AuditService auditService
    ) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.auditService = auditService;
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
    public ResponseEntity<Page<UserResponse>> listUsers(
            @ParameterObject Pageable pageable,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Page<UserResponse> page = users.findAll(pageable)
            .map(u -> new UserResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPapeis().stream().map(Papel::getNome).toList(),
                u.getStatus().name()
            ));

        // Audit
        auditService.log(
                "ADMIN_LIST_USERS",
                authentication != null ? authentication.getName() : "SYSTEM",
                request,
                Map.of(
                        "pageNumber", pageable.getPageNumber(),
                        "pageSize", pageable.getPageSize(),
                        "totalElements", page.getTotalElements()
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(page);
    }

    // ------------------------------------------------------------
    // /users - Criar usuário (público)
    // ------------------------------------------------------------
    @Operation(summary = "Criar usuário (público)")
    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest req,
            HttpServletRequest request
    ) {
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
        u.setStatus(UserStatus.ACTIVE); // ou ACTIVE se quiser liberar acesso imediato
        u.getPapeis().add(userRole);

        u = users.save(u);

        var resp = new UserResponse(
            u.getId(),
            u.getNome(),
            u.getEmail(),
            u.getPapeis().stream().map(Papel::getNome).toList(),
            u.getStatus().name()
        );

        // Audit
        auditService.log(
                "USER_CREATED",
                u.getEmail(), // quem foi criado
                request,
                Map.of(
                        "userId", u.getId(),
                        "status", u.getStatus().name()
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
}
