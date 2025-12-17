package com.guideaut.project.artefatos.repository;

import com.guideaut.project.artefatos.model.Empathy;
import com.guideaut.project.identity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EmpathyRepository extends JpaRepository<Empathy, UUID> {
    List<Empathy> findAllByUsuario (Usuario usuario);
}