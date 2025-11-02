package com.guideaut.project.auth;

import com.guideaut.project.auth.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService s) {
        this.service = s;
    }

    @Operation(summary = "Login (gera access e refresh)")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req, HttpServletRequest r) {
        return ResponseEntity.ok(service.login(req, r.getRemoteAddr(), r.getHeader("User-Agent")));
    }

    // DTO simples para o body de refresh/logout
    public record RefreshRequest(String refreshToken) {}

    @Operation(summary = "Troca refresh por novo access (com rotação de refresh)")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
        return ResponseEntity.ok(service.refresh(req.refreshToken()));
    }

    @Operation(summary = "Logout (revoga o refresh informado)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest req) {
        service.logout(req.refreshToken());
        return ResponseEntity.ok().build();
    }
}
