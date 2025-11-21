package com.guideaut.project.repo;

import com.guideaut.project.recomendacao.Recomendacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RecomendacaoRepo extends JpaRepository<Recomendacao, UUID> {
}