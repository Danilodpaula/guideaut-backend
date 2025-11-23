package com.guideaut.project.artefatos.repository;

import com.guideaut.project.artefatos.model.Empathy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmpathyRepository extends JpaRepository<Empathy, UUID> {}