package com.guideaut.project.audit;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String evento; // LOGIN_SUCCESS, LOGIN_FAIL, etc.

    private String usuarioEmail;
    private String ip;
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String detalhesJson;

    @Column(nullable = false)
    private OffsetDateTime timestamp = OffsetDateTime.now();

    // getters/setters
    public UUID getId() {
        return id;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetalhesJson() {
        return detalhesJson;
    }

    public void setDetalhesJson(String detalhesJson) {
        this.detalhesJson = detalhesJson;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
