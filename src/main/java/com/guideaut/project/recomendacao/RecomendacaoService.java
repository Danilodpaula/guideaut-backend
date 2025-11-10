package com.guideaut.project.recomendacao;

import com.guideaut.project.recomendacao.dto.RecomendacaoRequest;
import com.guideaut.project.repo.RecomendacaoRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecomendacaoService {

    private final RecomendacaoRepo recomendacaoRepo;

    public RecomendacaoService(RecomendacaoRepo recomendacaoRepo) {
        this.recomendacaoRepo = recomendacaoRepo;
    }

    /**
     * Lista todas as recomendações.
     */
    public List<Recomendacao> listarTodas() {
        // Simplesmente retorna todas
        return recomendacaoRepo.findAll();
    }

    /**
     * Cria uma nova recomendação.
     * @param request O DTO com os dados.
     */
    public Recomendacao criar(RecomendacaoRequest request) {
        Recomendacao nova = new Recomendacao();
        nova.setTitulo(request.titulo());
        nova.setDescricao(request.descricao());
        nova.setCategoria(request.categoria());
        nova.setReferencia(request.referencia());
        
        
        return recomendacaoRepo.save(nova);
    }
}