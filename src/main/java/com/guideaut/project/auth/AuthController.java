package com.guideaut.project.auth;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.auth.dto.AuthRequest;
import com.guideaut.project.auth.dto.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final AuditService auditService;

    public AuthController(AuthService s, AuditService auditService) {
        this.service = s;
        this.auditService = auditService;
    }

    @Operation(summary = "Login (gera access e refresh)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest req,
            HttpServletRequest r
    ) {
        try {
            AuthResponse resp = service.login(
                    req,
                    r.getRemoteAddr(),
                    r.getHeader("User-Agent")
            );

            // LOGIN_SUCCESS
            auditService.log(
                    "LOGIN_SUCCESS",
                    req.email(), // usuário que tentou logar
                    r,
                    Map.of(
                            "email", req.email()
                    ),
                    AuditSeverity.INFO
            );

            return ResponseEntity.ok(resp);
        } catch (RuntimeException ex) {
            // LOGIN_FAILED
            auditService.log(
                    "LOGIN_FAILED",
                    req.email(),
                    r,
                    Map.of(
                            "email", req.email(),
                            "reason", ex.getMessage()
                    ),
                    AuditSeverity.ERROR
            );
            throw ex; // deixa o Spring devolver 401/403/500 conforme o AuthService
        }
    }

    // DTO simples para o body de refresh/logout
    public record RefreshRequest(String refreshToken) {}

    @Operation(summary = "Troca refresh por novo access (com rotação de refresh)")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshRequest req,
            HttpServletRequest r
    ) {
        AuthResponse resp = service.refresh(req.refreshToken());

        // REFRESH_TOKEN_USED (sem logar o token inteiro)
        auditService.log(
                "REFRESH_TOKEN_ROTATED",
                null, // se quiser, pode puxar email no AuthService e retornar
                r,
                Map.of(
                        "refreshTokenSnippet", maskToken(req.refreshToken())
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Logout (revoga o refresh informado)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody RefreshRequest req,
            HttpServletRequest r
    ) {
        service.logout(req.refreshToken());

        // LOGOUT
        auditService.log(
                "LOGOUT",
                null, // se você quiser, pode adaptar AuthService.logout para retornar o e-mail
                r,
                Map.of(
                        "refreshTokenSnippet", maskToken(req.refreshToken())
                ),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok().build();
    }

    /**
     * Evita registrar o refresh token inteiro no log.
     * Mostra só o início, para debug, sem comprometer segurança.
     */
    private String maskToken(String token) {
        if (token == null) return null;
        int visible = Math.min(10, token.length());
        return token.substring(0, visible) + "...";
    }
}
