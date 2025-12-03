package com.guideaut.project.repo;

import com.guideaut.project.identity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepo extends JpaRepository<Usuario, UUID> {
    
    // O Spring Data JPA lê esse nome e gera o SQL automaticamente:
    // SELECT * FROM usuarios WHERE email = ?
    Optional<Usuario> findByEmail(String email);
    
}