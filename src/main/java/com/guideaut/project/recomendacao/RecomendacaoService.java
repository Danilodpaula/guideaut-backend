package com.guideaut.project.recomendacao;

import com.guideaut.project.identity.Usuario; 
import com.guideaut.project.recomendacao.dto.AvaliacaoRequest;
import com.guideaut.project.recomendacao.dto.RecomendacaoRequest;
import com.guideaut.project.repo.RecomendacaoAvaliacaoRepo; 
import com.guideaut.project.repo.RecomendacaoRepo;
import com.guideaut.project.repo.UsuarioRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecomendacaoService {

    private final RecomendacaoRepo recomendacaoRepo;
    private final UsuarioRepo usuarioRepo;
    private final RecomendacaoAvaliacaoRepo avaliacaoRepo; 

    public RecomendacaoService(
            RecomendacaoRepo recomendacaoRepo, 
            UsuarioRepo usuarioRepo, 
            RecomendacaoAvaliacaoRepo avaliacaoRepo
    ) {
        this.recomendacaoRepo = recomendacaoRepo;
        this.usuarioRepo = usuarioRepo;
        this.avaliacaoRepo = avaliacaoRepo;
    }

     public List<Recomendacao> listarTodas() {
        return recomendacaoRepo.findAll();
    }

    public Recomendacao criar(RecomendacaoRequest request) {
        Recomendacao nova = new Recomendacao();
        nova.setTitulo(request.titulo());
        nova.setDescricao(request.descricao());
        nova.setJustificativa(request.justificativa());
        nova.setCategoria(request.categoria());
        nova.setReferencia(request.referencia());
        
        return recomendacaoRepo.save(nova);
    }

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

    public void deletar(UUID id) {
        if (!recomendacaoRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não encontrada");
        }
        recomendacaoRepo.deleteById(id);
    }


    /**
     * @param id 
     * @param request 
     * @param autorEmail 
     */
    public Recomendacao avaliar(UUID id, AvaliacaoRequest request, String autorEmail) {
        if (request.nota() < 1 || request.nota() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nota deve ser entre 1 e 5");
        }
        
        Usuario usuario = usuarioRepo.findByEmail(autorEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));
        
        Recomendacao recomendacao = recomendacaoRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não encontrada"));

        Optional<RecomendacaoAvaliacao> avaliacaoExistente = avaliacaoRepo.findByUsuarioAndRecomendacao(usuario, recomendacao);

        int notaAntiga = 0;
        RecomendacaoAvaliacao avaliacaoParaSalvar;

        if (avaliacaoExistente.isPresent()) {
            avaliacaoParaSalvar = avaliacaoExistente.get();
            notaAntiga = avaliacaoParaSalvar.getNota();
        } else {
            avaliacaoParaSalvar = new RecomendacaoAvaliacao();
            avaliacaoParaSalvar.setUsuario(usuario);
            avaliacaoParaSalvar.setRecomendacao(recomendacao);
        }


        recomendacao.setSomaNotas(recomendacao.getSomaNotas() - notaAntiga + request.nota());
        
        if (!avaliacaoExistente.isPresent()) {
            recomendacao.setTotalAvaliacoes(recomendacao.getTotalAvaliacoes() + 1);
        }

        avaliacaoParaSalvar.setNota(request.nota());
        avaliacaoRepo.save(avaliacaoParaSalvar);

        return recomendacaoRepo.save(recomendacao);
    }
}