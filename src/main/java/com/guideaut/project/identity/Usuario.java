package com.guideaut.project.identity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
})
public class Usuario implements UserDetails { // <--- MUDANÇA: Implementa UserDetails

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

    /** Nome de exibição (opcional, diferente de "nome" completo). */
    @Column(name = "display_name")
    private String displayName;

    /** Biografia curta do usuário (até 500 caracteres). */
    @Column(name = "bio", length = 500)
    private String bio;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER é necessário para carregar permissões no login
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

    // =================================================================
    // MÉTODOS OBRIGATÓRIOS DO USER DETAILS (SPRING SECURITY)
    // =================================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte a lista de Papeis (ex: Papel "ADMIN") para SimpleGrantedAuthority (ex: "ROLE_ADMIN")
        // O Spring precisa disso para saber se pode acessar rotas .hasRole()
        return papeis.stream()
                .map(papel -> new SimpleGrantedAuthority(papel.getNome())) 
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.passwordHash; // Aponta para o campo correto do banco
    }

    @Override
    public String getUsername() {
        return this.email; // O "login" é o email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Se quiser bloquear usuários no futuro, mude lógica aqui
        return this.status != UserStatus.BLOCKED; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Só permite login se estiver ATIVO (ou ajuste conforme sua regra de negócio)
        return this.status == UserStatus.ACTIVE;
    }

    // =================================================================
    // GETTERS E SETTERS PADRÃO
    // =================================================================

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

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Papel> getPapeis() {
        return papeis;
    }

    public Set<Permissao> getPermissoesDiretas() {
        return permissoesDiretas;
    }

    @PreUpdate
    public void onUpdate() {
        this.atualizadoEm = OffsetDateTime.now();
    }
}