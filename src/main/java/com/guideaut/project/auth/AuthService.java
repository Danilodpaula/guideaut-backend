package com.guideaut.project.auth;

import com.guideaut.project.auth.dto.*;
import com.guideaut.project.audit.AuditLog;
import com.guideaut.project.repo.*;
import com.guideaut.project.security.JwtService;
import com.guideaut.project.token.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    private final UsuarioRepo usuarios;
    private final RefreshTokenRepo refreshRepo;
    private final AuditLogRepo auditRepo;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UsuarioRepo u, RefreshTokenRepo r, AuditLogRepo a, JwtService j, BCryptPasswordEncoder e) {
        usuarios = u;
        refreshRepo = r;
        auditRepo = a;
        jwt = j;
        encoder = e;
    }

    public AuthResponse login(AuthRequest req, String ip, String ua) {
        var user = usuarios.findByEmail(req.email())
                .orElseThrow(() -> unauthorized("Credenciais inválidas"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            auditRepo.save(audit("LOGIN_FAIL", req.email(), ip, ua, null));
            throw unauthorized("Credenciais inválidas");
        }

        var claims = Map.<String, Object>of(
                "roles", user.getPapeis().stream().map(p -> p.getNome()).toArray(String[]::new)
        );

        String access = jwt.generateAccess(user.getEmail(), claims);

        String rawRefresh = UUID.randomUUID().toString();
        var token = new RefreshToken();
        token.setUsuario(user);
        token.setTokenHash(sha256(rawRefresh));
        token.setExpiraEm(OffsetDateTime.now().plusDays(7));
        refreshRepo.save(token);

        auditRepo.save(audit("LOGIN_SUCCESS", user.getEmail(), ip, ua, null));
        return new AuthResponse(access, rawRefresh);
    }

    /** Troca refresh válido por novo access (e gira o refresh). */
    public AuthResponse refresh(String rawRefresh) {
        var token = refreshRepo.findByTokenHash(sha256(rawRefresh))
                .orElseThrow(() -> unauthorized("Refresh inválido"));

        if (token.getExpiraEm().isBefore(OffsetDateTime.now())) {
            refreshRepo.delete(token);
            throw unauthorized("Refresh expirado");
        }

        var user = token.getUsuario();
        var claims = Map.<String, Object>of(
                "roles", user.getPapeis().stream().map(p -> p.getNome()).toArray(String[]::new)
        );
        String newAccess = jwt.generateAccess(user.getEmail(), claims);

        // rotação: invalida o antigo e cria outro
        refreshRepo.delete(token);
        String newRawRefresh = UUID.randomUUID().toString();
        var newToken = new RefreshToken();
        newToken.setUsuario(user);
        newToken.setTokenHash(sha256(newRawRefresh));
        newToken.setExpiraEm(OffsetDateTime.now().plusDays(7));
        refreshRepo.save(newToken);

        auditRepo.save(audit("REFRESH_SUCCESS", user.getEmail(), null, null, null));
        return new AuthResponse(newAccess, newRawRefresh);
    }

    /** Revoga o refresh informado (logout). */
    public void logout(String rawRefresh) {
        refreshRepo.findByTokenHash(sha256(rawRefresh)).ifPresent(refreshRepo::delete);
        // (opcional) revogar todos os refresh do usuário
        // refreshRepo.deleteAllByUsuarioId(...);
    }

    // ----- helpers -----
    private AuditLog audit(String evt, String email, String ip, String ua, String details) {
        var a = new AuditLog();
        a.setEvento(evt);
        a.setUsuarioEmail(email);
        a.setIp(ip);
        a.setUserAgent(ua);
        a.setDetalhesJson(details);
        return a;
    }

    private String sha256(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            var sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseStatusException unauthorized(String msg) {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
    }
}
