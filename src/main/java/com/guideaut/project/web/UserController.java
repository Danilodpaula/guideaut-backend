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
@Tag(name = "Usuário") // organiza no Swagger
public class UserController {

        private final UsuarioRepo users;
        private final PapelRepo roles;
        private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder;
        private final AuditService auditService;

        public UserController(
                        UsuarioRepo users,
                        PapelRepo roles,
                        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder,
                        AuditService auditService) {
                this.users = users;
                this.roles = roles;
                this.encoder = encoder;
                this.auditService = auditService;
        }

        // helper pra montar UserResponse sempre igual
        private UserResponse toUserResponse(Usuario u) {
                return new UserResponse(
                                u.getId(),
                                u.getNome(),
                                u.getEmail(),
                                u.getPapeis().stream().map(Papel::getNome).toList(),
                                u.getStatus().name());
        }

        // IMPORTANTE: o endpoint /me foi movido para ProfileController
        // para incluir avatarUrl. Não deixe outro /me aqui para evitar conflito.

        // ------------------------------------------------------------
        // /admin/users - Listar usuários (ADMIN), paginado
        // ------------------------------------------------------------
        @Operation(summary = "Listar usuários (ADMIN)", description = "Retorna a lista paginada de usuários. Requer papel ADMIN.", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @PreAuthorize("hasAuthority('ADMIN')")
        @GetMapping("/admin/users")
        public ResponseEntity<Page<UserResponse>> listUsers(
                        @ParameterObject Pageable pageable,
                        Authentication authentication,
                        HttpServletRequest request) {
                Page<UserResponse> page = users.findAll(pageable)
                                .map(this::toUserResponse);

                auditService.log(
                                "ADMIN_LIST_USERS",
                                authentication != null ? authentication.getName() : "SYSTEM",
                                request,
                                Map.of(
                                                "pageNumber", pageable.getPageNumber(),
                                                "pageSize", pageable.getPageSize(),
                                                "totalElements", page.getTotalElements()),
                                AuditSeverity.INFO);

                return ResponseEntity.ok(page);
        }

        // ------------------------------------------------------------
        // /users - Criar usuário (público)
        // ------------------------------------------------------------
        @Operation(summary = "Criar usuário (público)")
        @PostMapping("/users")
        public ResponseEntity<UserResponse> create(
                        @Valid @RequestBody CreateUserRequest req,
                        HttpServletRequest request) {
                if (users.findByEmail(req.email()).isPresent()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
                }

                var userRole = roles.findByNome("USER").orElseGet(() -> {
                        var r = new Papel();
                        r.setNome("USER");
                        return roles.save(r);
                });

                var u = new Usuario();
                u.setNome(req.nome());
                u.setEmail(req.email());
                u.setPasswordHash(encoder.encode(req.password()));
                u.setStatus(UserStatus.ACTIVE);
                u.getPapeis().add(userRole);

                u = users.save(u);

                var resp = toUserResponse(u);

                auditService.log(
                                "USER_CREATED",
                                u.getEmail(),
                                request,
                                Map.of(
                                                "userId", u.getId(),
                                                "status", u.getStatus().name()),
                                AuditSeverity.INFO);

                return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        }

        // ------------------------------------------------------------
        // PATCH /admin/users/{id}/status - alterar status (ADMIN)
        // body: { "status": "ACTIVE" | "BLOCKED" | "PENDING" | "ARCHIVED" }
        // ------------------------------------------------------------
        @Operation(summary = "Atualizar status de usuário (ADMIN)", description = "Permite ao administrador mudar status do usuário (PENDING, ACTIVE, BLOCKED, ARCHIVED).", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @PreAuthorize("hasAuthority('ADMIN')")
        @PatchMapping("/admin/users/{id}/status")
        public ResponseEntity<UserResponse> updateUserStatus(
                        @PathVariable("id") java.util.UUID id,
                        @Valid @RequestBody com.guideaut.project.web.dto.AdminUpdateStatusRequest body,
                        Authentication authentication,
                        HttpServletRequest request) {
                var user = users.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Usuário não encontrado"));

                user.setStatus(body.status());
                user = users.save(user);

                // Audit
                auditService.log(
                                "ADMIN_UPDATE_USER_STATUS",
                                authentication != null ? authentication.getName() : "SYSTEM",
                                request,
                                Map.of(
                                                "targetUserId", user.getId(),
                                                "newStatus", user.getStatus().name()),
                                AuditSeverity.WARNING);

                return ResponseEntity.ok(toUserResponse(user));
        }

        // ------------------------------------------------------------
        // PATCH /admin/users/{id}/roles - atualizar papeis (ADMIN)
        // body: { "roles": ["ADMIN", "USER"] }
        // ------------------------------------------------------------
        @Operation(summary = "Atualizar papeis de usuário (ADMIN)", description = "Atualiza completamente o conjunto de papeis do usuário com base nos nomes enviados.", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @PreAuthorize("hasAuthority('ADMIN')")
        @PatchMapping("/admin/users/{id}/roles")
        public ResponseEntity<UserResponse> updateUserRoles(
                        @PathVariable("id") java.util.UUID id,
                        @Valid @RequestBody com.guideaut.project.web.dto.AdminUpdateRolesRequest body,
                        Authentication authentication,
                        HttpServletRequest request) {
                var user = users.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Usuário não encontrado"));

                // carrega os papeis pelos nomes enviados
                var novosPapeis = body.roles().stream()
                                .map(nome -> roles.findByNome(nome)
                                                .orElseThrow(() -> new ResponseStatusException(
                                                                HttpStatus.NOT_FOUND, "Papel não encontrado: " + nome)))
                                .collect(java.util.stream.Collectors.toSet());

                user.getPapeis().clear();
                user.getPapeis().addAll(novosPapeis);

                user = users.save(user);

                // Audit
                auditService.log(
                                "ADMIN_UPDATE_USER_ROLES",
                                authentication != null ? authentication.getName() : "SYSTEM",
                                request,
                                Map.of(
                                                "targetUserId", user.getId(),
                                                "newRoles", user.getPapeis().stream().map(Papel::getNome).toList()),
                                AuditSeverity.WARNING);

                return ResponseEntity.ok(toUserResponse(user));
        }

        // ------------------------------------------------------------
        // POST /admin/users/{id}/reset-password - resetar senha (ADMIN)
        // ------------------------------------------------------------
        @Operation(summary = "Resetar senha de usuário (ADMIN)", description = "Gera uma senha temporária para o usuário e sobrescreve a anterior.", security = {
                        @SecurityRequirement(name = "bearerAuth") })
        @PreAuthorize("hasAuthority('ADMIN')")
        @PostMapping("/admin/users/{id}/reset-password")
        public ResponseEntity<Void> resetUserPassword(
                        @PathVariable("id") java.util.UUID id,
                        Authentication authentication,
                        HttpServletRequest request) {
                var user = users.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Usuário não encontrado"));

                // senha temporária simples (melhorar depois com gerador mais robusto)
                String tempPassword = java.util.UUID.randomUUID().toString().substring(0, 8);

                user.setPasswordHash(encoder.encode(tempPassword));
                users.save(user);

                // Aqui você decide sua estratégia:
                // - Enviar por e-mail pro usuário
                // - Exibir em algum painel de admin
                // Por segurança, eu não coloco a senha em logs/auditoria.

                auditService.log(
                                "ADMIN_RESET_USER_PASSWORD",
                                authentication != null ? authentication.getName() : "SYSTEM",
                                request,
                                Map.of(
                                                "targetUserId", user.getId()),
                                AuditSeverity.WARNING);

                // Por enquanto, só 204 mesmo. Depois você pode mudar o contrato
                // se quiser retornar algo mais.
                return ResponseEntity.noContent().build();
        }

}
