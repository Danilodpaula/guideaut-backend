package com.guideaut.project.artefatos.repository;

import com.guideaut.project.artefatos.model.Persona;
import com.guideaut.project.identity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PersonaRepository extends JpaRepository<Persona, UUID> {
    List<Persona> findAllByUsuario (Usuario usuario);
}