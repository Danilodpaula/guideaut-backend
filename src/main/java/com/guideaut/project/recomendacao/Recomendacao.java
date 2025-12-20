package com.guideaut.project.recomendacao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guideaut.project.identity.Usuario;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Table(name = "recomendacoes")
public class Recomendacao {
    
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column
    private String referencia;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String justificativa;

    @Column(nullable = false)
    private String categoria;

    @Column
    private int somaNotas = 0;

    @Column
    private int totalAvaliacoes = 0;

    @Column(nullable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @JsonProperty("usuarioId")
    public UUID getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    @OneToMany(
    mappedBy = "recomendacao",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
    private List<RecomendacaoComentario> comentarios;

    @OneToMany(
    mappedBy = "recomendacao",
    cascade = CascadeType.ALL,
    orphanRemoval = true
)
    private List<RecomendacaoAvaliacao> avaliacoes;


    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public int getSomaNotas() { return somaNotas; }
    public void setSomaNotas(int somaNotas) { this.somaNotas = somaNotas; }
    public int getTotalAvaliacoes() { return totalAvaliacoes; }
    public void setTotalAvaliacoes(int totalAvaliacoes) { this.totalAvaliacoes = totalAvaliacoes; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}