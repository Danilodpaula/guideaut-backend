package com.guideaut.project.recomendacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRecomendacaoRepository extends JpaRepository<CategoriaRecomendacao, Long> {
    
    
    Optional<CategoriaRecomendacao> findByNome(String nome);
    
    boolean existsByNome(String nome);
}