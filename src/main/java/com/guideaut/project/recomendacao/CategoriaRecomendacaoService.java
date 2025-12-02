package com.guideaut.project.recomendacao;

import com.guideaut.project.recomendacao.dto.CategoriaRecomendacaoDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriaRecomendacaoService {
    
    
    private final CategoriaRecomendacaoRepository repository;
    
    @Transactional
    public CategoriaRecomendacaoDTO criar(CategoriaRecomendacaoDTO dto) {
        if (repository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Categoria com este nome já existe");
        }
        
        CategoriaRecomendacao categoria = CategoriaRecomendacao.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .ativo(true)
                .build();
        
        CategoriaRecomendacao salva = repository.save(categoria);
        return converterParaDTO(salva);
    }
    
    @Transactional(readOnly = true)
    public List<CategoriaRecomendacaoDTO> listar() {
        return repository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoriaRecomendacaoDTO obterPorId(Long id) {
        CategoriaRecomendacao categoria = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        return converterParaDTO(categoria);
    }
    
    @Transactional
    public CategoriaRecomendacaoDTO atualizar(Long id, CategoriaRecomendacaoDTO dto) {
        CategoriaRecomendacao categoria = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));
        
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());
        categoria.setAtivo(dto.getAtivo());
        
        CategoriaRecomendacao atualizada = repository.save(categoria);
        return converterParaDTO(atualizada);
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Categoria não encontrada");
        }
        repository.deleteById(id);
    }
    
    private CategoriaRecomendacaoDTO converterParaDTO(CategoriaRecomendacao categoria) {
        return CategoriaRecomendacaoDTO.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativo(categoria.getAtivo())
                .build();
    }
}