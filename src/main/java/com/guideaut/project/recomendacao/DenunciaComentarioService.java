package com.guideaut.project.recomendacao;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.recomendacao.dto.CreateDenunciaComentarioDto;
import com.guideaut.project.recomendacao.dto.DenunciaComentarioResponse;
import com.guideaut.project.repo.DenunciaComentarioRepo;
import com.guideaut.project.repo.RecomendacaoComentarioRepo;
import com.guideaut.project.repo.UsuarioRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DenunciaComentarioService {

    private final DenunciaComentarioRepo denunciaRepo;
    private final RecomendacaoComentarioRepo comentarioRepo;
    private final UsuarioRepo usuarioRepo;

    public DenunciaComentarioService(
            DenunciaComentarioRepo denunciaRepo,
            RecomendacaoComentarioRepo comentarioRepo,
            UsuarioRepo usuarioRepo
    ) {
        this.denunciaRepo = denunciaRepo;
        this.comentarioRepo = comentarioRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public List<DenunciaComentario> listarTodas() {
        return denunciaRepo.findAll();
    }

    public List<DenunciaComentario> listarPorStatus(String status) {
        return denunciaRepo.findByStatusOrderByCriadoEmDesc(status);
    }

    public List<DenunciaComentario> listarPorComentario(UUID comentarioId) {
        return denunciaRepo.findByComentarioIdOrderByCriadoEmDesc(comentarioId);
    }

    public DenunciaComentario criar(UUID comentarioId, CreateDenunciaComentarioDto request, String denunciadorEmail) {
        Usuario denunciador = usuarioRepo.findByEmail(denunciadorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        RecomendacaoComentario comentario = comentarioRepo.findById(comentarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        // Verifica se o usuário já denunciou este comentário
        long denunciasExistentes = denunciaRepo.countByComentarioAndUsuario(comentarioId, denunciador.getId());
        if (denunciasExistentes > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já denunciou este comentário");
        }

        DenunciaComentario denuncia = new DenunciaComentario();
        denuncia.setUsuario(denunciador);
        denuncia.setComentario(comentario);
        denuncia.setMotivo(request.motivo());
        denuncia.setDescricao(request.descricao());
        denuncia.setStatus("ABERTA");

        return denunciaRepo.save(denuncia);
    }

    public DenunciaComentario atualizarStatus(UUID denunciaId, String novoStatus, String autorEmail) {
        DenunciaComentario denuncia = denunciaRepo.findById(denunciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada"));

        if (!isStatusValido(novoStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        }

        denuncia.setStatus(novoStatus);
        denuncia.setAtualizadoEm(OffsetDateTime.now());

        return denunciaRepo.save(denuncia);
    }

    public void deletar(UUID denunciaId) {
        if (!denunciaRepo.existsById(denunciaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada");
        }
        denunciaRepo.deleteById(denunciaId);
    }

    @Transactional(readOnly = true)
    public List<DenunciaComentarioResponse> listarTodasResponse() {
        List<DenunciaComentario> denuncias = denunciaRepo.findAll();
        return denuncias.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DenunciaComentarioResponse> listarPorStatusResponse(String status) {
        List<DenunciaComentario> denuncias = denunciaRepo.findByStatusOrderByCriadoEmDesc(status);
        return denuncias.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DenunciaComentarioResponse> listarPorComentarioResponse(UUID comentarioId) {
        List<DenunciaComentario> denuncias = denunciaRepo.findByComentarioIdOrderByCriadoEmDesc(comentarioId);
        return denuncias.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public DenunciaComentarioResponse obterResponse(UUID denunciaId) {
        DenunciaComentario denuncia = denunciaRepo.findById(denunciaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada"));
        return mapToResponse(denuncia);
    }

    private DenunciaComentarioResponse mapToResponse(DenunciaComentario denuncia) {
        RecomendacaoComentario comentario = denuncia.getComentario();
        Usuario autorComentario = comentario.getUsuario();
        
        return new DenunciaComentarioResponse(
            denuncia.getId(),
            comentario.getId(),
            comentario.getTexto(),
            autorComentario.getNome(),
            autorComentario.getAvatarPath() != null ? "/files/" + autorComentario.getAvatarPath() : null,
            denuncia.getMotivo(),
            denuncia.getDescricao(),
            denuncia.getStatus(),
            denuncia.getUsuario().getNome(),
            denuncia.getCriadoEm(),
            denuncia.getAtualizadoEm()
        );
    }

    private boolean isStatusValido(String status) {
        return status.equals("ABERTA") || status.equals("EM_ANÁLISE") || status.equals("RESOLVIDA") || status.equals("REJEITADA");
    }
}
