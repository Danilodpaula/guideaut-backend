package com.guideaut.project.auth;

import com.guideaut.project.auth.dto.AuthRequest;
import com.guideaut.project.auth.dto.AuthResponse;
import com.guideaut.project.auth.dto.ForgotPasswordRequest;
import com.guideaut.project.auth.dto.ResetPasswordWithCodeRequest;
import com.guideaut.project.audit.AuditLog;
import com.guideaut.project.audit.AuditSeverity;
import com.guideaut.project.identity.UserStatus;
import com.guideaut.project.mail.EmailService;
import com.guideaut.project.repo.AuditLogRepo;
import com.guideaut.project.repo.PasswordResetCodeRepo;
import com.guideaut.project.repo.RefreshTokenRepo;
import com.guideaut.project.repo.UsuarioRepo;
import com.guideaut.project.security.JwtService;
import com.guideaut.project.token.PasswordResetCode;
import com.guideaut.project.token.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
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

    private final PasswordResetCodeRepo passwordResetCodeRepo;
    private final EmailService emailService;

    public AuthService(
            UsuarioRepo u,
            RefreshTokenRepo r,
            AuditLogRepo a,
            JwtService j,
            BCryptPasswordEncoder e,
            PasswordResetCodeRepo passwordResetCodeRepo,
            EmailService emailService
    ) {
        this.usuarios = u;
        this.refreshRepo = r;
        this.auditRepo = a;
        this.jwt = j;
        this.encoder = e;
        this.passwordResetCodeRepo = passwordResetCodeRepo;
        this.emailService = emailService;
    }

    // =========================================================
    // LOGIN / TOKENS
    // =========================================================

    public AuthResponse login(AuthRequest req, String ip, String ua) {
        var user = usuarios.findByEmail(req.email())
                .orElseThrow(() -> unauthorized("Credenciais inválidas"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            // LOGIN_FAILED
            auditRepo.save(audit(
                    "LOGIN_FAILED",
                    req.email(),
                    ip,
                    ua,
                    "Senha inválida",
                    AuditSeverity.ERROR
            ));
            throw unauthorized("Credenciais inválidas");
        }

        // ❗ Bloqueia usuários que não estão ATIVOS
        if (user.getStatus() != UserStatus.ACTIVE) {
            auditRepo.save(audit(
                    "LOGIN_BLOCKED_STATUS",
                    user.getEmail(),
                    ip,
                    ua,
                    "Status=" + user.getStatus(),
                    AuditSeverity.WARNING
            ));
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Usuário não está ativo"
            );
        }

        // MUDANÇA AQUI: Usa o novo método do JwtService que extrai as roles automaticamente
        String access = jwt.generateToken(user);

        String rawRefresh = UUID.randomUUID().toString();
        var token = new RefreshToken();
        token.setUsuario(user);
        token.setTokenHash(sha256(rawRefresh));
        token.setExpiraEm(OffsetDateTime.now().plusDays(7));
        refreshRepo.save(token);

        // LOGIN_SUCCESS
        auditRepo.save(audit(
                "LOGIN_SUCCESS",
                user.getEmail(),
                ip,
                ua,
                null,
                AuditSeverity.INFO
        ));

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
        
        // MUDANÇA AQUI TAMBÉM: Usa generateToken(user)
        String newAccess = jwt.generateToken(user);

        // rotação: invalida o antigo e cria outro
        refreshRepo.delete(token);
        String newRawRefresh = UUID.randomUUID().toString();
        var newToken = new RefreshToken();
        newToken.setUsuario(user);
        newToken.setTokenHash(sha256(newRawRefresh));
        newToken.setExpiraEm(OffsetDateTime.now().plusDays(7));
        refreshRepo.save(newToken);

        // REFRESH_SUCCESS
        auditRepo.save(audit(
                "REFRESH_SUCCESS",
                user.getEmail(),
                null,
                null,
                null,
                AuditSeverity.INFO
        ));

        return new AuthResponse(newAccess, newRawRefresh);
    }

    /** Revoga o refresh informado (logout). */
    public void logout(String rawRefresh) {
        refreshRepo.findByTokenHash(sha256(rawRefresh)).ifPresent(token -> {
            var user = token.getUsuario();
            String email = user != null ? user.getEmail() : null;

            refreshRepo.delete(token);

            // LOGOUT
            auditRepo.save(audit(
                    "LOGOUT",
                    email,
                    null,
                    null,
                    null,
                    AuditSeverity.INFO
            ));
        });
    }

    // =========================================================
    // FORGOT PASSWORD / RESET COM CÓDIGO
    // =========================================================

    public void requestPasswordReset(ForgotPasswordRequest req, String ip, String ua) {
        var optUser = usuarios.findByEmail(req.email());

        if (optUser.isEmpty()) {
            auditRepo.save(audit(
                    "FORGOT_PASSWORD_UNKNOWN_EMAIL",
                    req.email(),
                    ip,
                    ua,
                    "Email não encontrado",
                    AuditSeverity.WARNING
            ));
            return;
        }

        var user = optUser.get();

        if (user.getStatus() == UserStatus.ARCHIVED) {
            auditRepo.save(audit(
                    "FORGOT_PASSWORD_ARCHIVED_USER",
                    user.getEmail(),
                    ip,
                    ua,
                    "Usuário arquivado",
                    AuditSeverity.WARNING
            ));
            return;
        }

        passwordResetCodeRepo.deleteAllByUsuario(user);

        String code = generateNumericCode(6);
        String codeHash = sha256(code);

        var prc = new PasswordResetCode();
        prc.setUsuario(user);
        prc.setCodeHash(codeHash);
        prc.setExpiraEm(OffsetDateTime.now().plusHours(1));
        passwordResetCodeRepo.save(prc);

        emailService.sendPasswordResetCodeEmail(user.getEmail(), code);

        auditRepo.save(audit(
                "FORGOT_PASSWORD_CODE_SENT",
                user.getEmail(),
                ip,
                ua,
                "Código enviado por e-mail",
                AuditSeverity.INFO
        ));
    }

    public void resetPasswordWithCode(ResetPasswordWithCodeRequest req, String ip, String ua) {
        var user = usuarios.findByEmail(req.email())
                .orElseThrow(() -> badRequest("Código inválido ou expirado"));

        var optCode = passwordResetCodeRepo
                .findTopByUsuarioAndUsadoEmIsNullOrderByCriadoEmDesc(user);

        if (optCode.isEmpty()) {
            throw badRequest("Código inválido ou expirado");
        }

        var codeEntity = optCode.get();

        if (codeEntity.isExpired()) {
            passwordResetCodeRepo.delete(codeEntity);
            throw badRequest("Código inválido ou expirado");
        }

        String incomingHash = sha256(req.code());
        if (!incomingHash.equals(codeEntity.getCodeHash())) {
            throw badRequest("Código inválido ou expirado");
        }

        codeEntity.setUsadoEm(OffsetDateTime.now());
        passwordResetCodeRepo.save(codeEntity);

        user.setPasswordHash(encoder.encode(req.newPassword()));
        usuarios.save(user);

        auditRepo.save(audit(
                "PASSWORD_RESET_SUCCESS",
                user.getEmail(),
                ip,
                ua,
                null,
                AuditSeverity.WARNING
        ));
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private AuditLog audit(
            String evt,
            String email,
            String ip,
            String ua,
            String details,
            AuditSeverity severity
    ) {
        var a = new AuditLog();
        a.setEvento(evt);
        a.setUsuarioEmail(email);
        a.setUserAgent(ua);
        a.setDetalhesJson(details);
        a.setSeverity(severity != null ? severity : AuditSeverity.INFO);
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

    private ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private String generateNumericCode(int length) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }
}