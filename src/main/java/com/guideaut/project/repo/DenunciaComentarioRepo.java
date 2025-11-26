package com.guideaut.project.repo;

import com.guideaut.project.recomendacao.DenunciaComentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DenunciaComentarioRepo extends JpaRepository<DenunciaComentario, UUID> {
    
    @Query("SELECT d FROM DenunciaComentario d JOIN FETCH d.usuario WHERE d.comentario.id = :comentarioId ORDER BY d.criadoEm DESC")
    List<DenunciaComentario> findByComentarioIdOrderByCriadoEmDesc(@Param("comentarioId") UUID comentarioId);

    @Query("SELECT d FROM DenunciaComentario d JOIN FETCH d.usuario WHERE d.status = :status ORDER BY d.criadoEm DESC")
    List<DenunciaComentario> findByStatusOrderByCriadoEmDesc(@Param("status") String status);

    @Query("SELECT COUNT(d) FROM DenunciaComentario d WHERE d.comentario.id = :comentarioId AND d.usuario.id = :usuarioId")
    long countByComentarioAndUsuario(@Param("comentarioId") UUID comentarioId, @Param("usuarioId") UUID usuarioId);
}
