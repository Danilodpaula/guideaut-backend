package com.guideaut.project.auth;

import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.auth.dto.AuthRequest;
import com.guideaut.project.auth.dto.AuthResponse;
import com.guideaut.project.auth.dto.ForgotPasswordRequest;
import com.guideaut.project.auth.dto.ResetPasswordWithCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // <--- FALTAVA ISSO
import org.springframework.security.core.context.SecurityContextHolder; // <--- FALTAVA ISSO
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Autenticação")
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
                    req.email(),
                    r,
                    Map.of("email", req.email()),
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
            throw ex;
        }
    }

    public record RefreshRequest(String refreshToken) {}

    @Operation(summary = "Troca refresh por novo access (com rotação de refresh)")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshRequest req,
            HttpServletRequest r
    ) {
        AuthResponse resp = service.refresh(req.refreshToken());

        auditService.log(
                "REFRESH_TOKEN_ROTATED",
                null,
                r,
                Map.of("refreshTokenSnippet", maskToken(req.refreshToken())),
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

        auditService.log(
                "LOGOUT",
                null,
                r,
                Map.of("refreshTokenSnippet", maskToken(req.refreshToken())),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok().build();
    }

    // =========================================================
    // ESQUECI MINHA SENHA
    // =========================================================

    @Operation(summary = "Solicitar código de redefinição de senha")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody ForgotPasswordRequest req,
            HttpServletRequest r
    ) {
        service.requestPasswordReset(
                req,
                r.getRemoteAddr(),
                r.getHeader("User-Agent")
        );

        auditService.log(
                "FORGOT_PASSWORD_REQUEST",
                req.email(),
                r,
                Map.of("email", req.email()),
                AuditSeverity.INFO
        );

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Redefinir senha usando código")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody ResetPasswordWithCodeRequest req,
            HttpServletRequest r
    ) {
        service.resetPasswordWithCode(
                req,
                r.getRemoteAddr(),
                r.getHeader("User-Agent")
        );

        auditService.log(
                "PASSWORD_RESET_REQUEST",
                req.email(),
                r,
                Map.of("email", req.email()),
                AuditSeverity.WARNING
        );

        return ResponseEntity.ok().build();
    }

    // =========================================================
    // ROTA DE DEBUG (Para descobrir por que dá 403)
    // =========================================================
    @GetMapping("/debug")
    public ResponseEntity<?> debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null) {
            return ResponseEntity.ok(Map.of("status", "Sem autenticação no contexto"));
        }

        return ResponseEntity.ok(Map.of(
                "principal", auth.getPrincipal().toString(),
                "authorities", auth.getAuthorities().stream().map(Object::toString).toList(),
                "isAuthenticated", auth.isAuthenticated()
        ));
    }

    private String maskToken(String token) {
        if (token == null) return null;
        int visible = Math.min(10, token.length());
        return token.substring(0, visible) + "...";
    }
}