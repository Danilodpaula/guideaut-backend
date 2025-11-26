package com.guideaut.project.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guideaut.project.repo.AuditLogRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuditService {

    private final AuditLogRepo repo;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepo repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    public void log(String event,
                    String usuarioEmail,
                    HttpServletRequest request,
                    Object detalhes,
                    AuditSeverity severity) {

        AuditLog log = new AuditLog();
        log.setEvento(event);
        log.setUsuarioEmail(usuarioEmail);
        log.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        log.setTimestamp(OffsetDateTime.now());
        log.setSeverity(severity != null ? severity : AuditSeverity.INFO);

        if (detalhes != null) {
            try {
                log.setDetalhesJson(objectMapper.writeValueAsString(detalhes));
            } catch (JsonProcessingException e) {
                // fallback se der erro ao serializar JSON
                log.setDetalhesJson(String.valueOf(detalhes));
            }
        }

        repo.save(log);
    }
}
