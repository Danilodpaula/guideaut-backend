package com.guideaut.project.repo;

import com.guideaut.project.identity.Usuario;
import com.guideaut.project.token.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetCodeRepo extends JpaRepository<PasswordResetCode, UUID> {

    void deleteAllByUsuario(Usuario usuario);

    Optional<PasswordResetCode> findTopByUsuarioAndUsadoEmIsNullOrderByCriadoEmDesc(Usuario usuario);
}
