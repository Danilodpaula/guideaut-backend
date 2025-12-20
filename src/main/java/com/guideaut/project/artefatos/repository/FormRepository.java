package com.guideaut.project.artefatos.repository;

import com.guideaut.project.artefatos.model.Form;
import com.guideaut.project.identity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FormRepository extends JpaRepository<Form, UUID> {
    List<Form> findAllByUsuario (Usuario usuario);
}