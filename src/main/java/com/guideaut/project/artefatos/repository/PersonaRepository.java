package com.guideaut.project.artefatos.repository;

import com.guideaut.project.artefatos.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PersonaRepository extends JpaRepository<Persona, UUID> {}