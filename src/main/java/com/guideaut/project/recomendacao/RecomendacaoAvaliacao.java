package com.guideaut.project.recomendacao;

import com.guideaut.project.identity.Usuario;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "recomendacao_avaliacoes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "recomendacao_id"})
})
public class RecomendacaoAvaliacao {
    
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Recomendacao recomendacao;

    @Column(nullable = false)
    private int nota;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Recomendacao getRecomendacao() { return recomendacao; }
    public void setRecomendacao(Recomendacao recomendacao) { this.recomendacao = recomendacao; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
}