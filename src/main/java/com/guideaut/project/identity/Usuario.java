package com.guideaut.project.identity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING;

    private OffsetDateTime emailVerificadoEm;
    private OffsetDateTime criadoEm = OffsetDateTime.now();
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    /** Caminho relativo do avatar sob a pasta de uploads (ex.: "avatars/<uuid>.jpg"). */
    @Column(name = "avatar_path")
    private String avatarPath;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_papel",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "papel_id")
    )
    private Set<Papel> papeis = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "papel_permissao",
            joinColumns = @JoinColumn(name = "papel_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    private Set<Permissao> permissoesDiretas = new HashSet<>();

    // ===========================
    // Getters / Setters
    // ===========================
    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public OffsetDateTime getEmailVerificadoEm() {
        return emailVerificadoEm;
    }
    public void setEmailVerificadoEm(OffsetDateTime e) {
        this.emailVerificadoEm = e;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
    public void setAtualizadoEm(OffsetDateTime a) {
        this.atualizadoEm = a;
    }

    public String getAvatarPath() {
        return avatarPath;
    }
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public Set<Papel> getPapeis() {
        return papeis;
    }

    public Set<Permissao> getPermissoesDiretas() {
        return permissoesDiretas;
    }

    // Atualiza automaticamente o timestamp de atualização
    @PreUpdate
    public void onUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}
