package com.guideaut.project.token;

import com.guideaut.project.identity.Usuario;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "ix_refresh_hash", columnList = "tokenHash", unique = true)
})
public class RefreshToken {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @Column(nullable = false, length = 64)
    private String tokenHash; // SHA-256(hex)

    @Column(nullable = false)
    private OffsetDateTime expiraEm;

    private OffsetDateTime revogadoEm;
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    // getters/setters
    public UUID getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public OffsetDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(OffsetDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    public OffsetDateTime getRevogadoEm() {
        return revogadoEm;
    }

    public void setRevogadoEm(OffsetDateTime revogadoEm) {
        this.revogadoEm = revogadoEm;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }
}
