package com.guideaut.project.recomendacao;

import com.guideaut.project.identity.Usuario;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "denuncia_comentarios")
public class DenunciaComentario {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RecomendacaoComentario comentario;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String status = "ABERTA";

    @Column(nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column
    private OffsetDateTime atualizadoEm;

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public RecomendacaoComentario getComentario() { return comentario; }
    public void setComentario(RecomendacaoComentario comentario) { this.comentario = comentario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
