package com.guideaut.project.recomendacao;

import com.guideaut.project.recomendacao.dto.RecomendacaoRequest;
import com.guideaut.project.repo.RecomendacaoRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; 
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;

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
        nova.setJustificativa(request.justificativa()); 
        nova.setCategoria(request.categoria());
        nova.setReferencia(request.referencia());
        
        return recomendacaoRepo.save(nova);
    }

    /**
     * ATUALIZA uma recomendação existente.
     * @param id O UUID da recomendação a ser atualizada.
     * @param request O DTO com os novos dados.
     */
    public Recomendacao atualizar(UUID id, RecomendacaoRequest request) {
        Recomendacao existente = recomendacaoRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não encontrada"));

        existente.setTitulo(request.titulo());
        existente.setDescricao(request.descricao());
        existente.setJustificativa(request.justificativa());
        existente.setCategoria(request.categoria());
        existente.setReferencia(request.referencia());
        
        return recomendacaoRepo.save(existente);
    }

    /**
     * DELETA uma recomendação.
     * @param id O UUID da recomendação a ser deletada.
     */
    public void deletar(UUID id) {
        // 1. Verifica se existe antes de deletar
        if (!recomendacaoRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não encontrada");
        }
        
        // 2. Deleta
        recomendacaoRepo.deleteById(id);
    }
}