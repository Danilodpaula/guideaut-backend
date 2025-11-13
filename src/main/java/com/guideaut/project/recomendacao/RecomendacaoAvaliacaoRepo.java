package com.guideaut.project.repo;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.recomendacao.Recomendacao;
import com.guideaut.project.recomendacao.RecomendacaoAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RecomendacaoAvaliacaoRepo extends JpaRepository<RecomendacaoAvaliacao, UUID> {
    
    Optional<RecomendacaoAvaliacao> findByUsuarioAndRecomendacao(Usuario usuario, Recomendacao recomendacao);
}