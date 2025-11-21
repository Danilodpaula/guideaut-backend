package com.guideaut.project.repo;

import com.guideaut.project.recomendacao.RecomendacaoComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecomendacaoComentarioRepo extends JpaRepository<RecomendacaoComentario, UUID> {
    
    @Query("SELECT c FROM RecomendacaoComentario c JOIN FETCH c.usuario WHERE c.recomendacao.id = :recomendacaoId ORDER BY c.criadoEm DESC")
    List<RecomendacaoComentario> findByRecomendacaoIdOrderByCriadoEmDesc(@Param("recomendacaoId") UUID recomendacaoId);
}