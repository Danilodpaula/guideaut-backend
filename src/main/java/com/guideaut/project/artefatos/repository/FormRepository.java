package com.guideaut.project.artefatos.repository;

import com.guideaut.project.artefatos.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FormRepository extends JpaRepository<Form, UUID> {}