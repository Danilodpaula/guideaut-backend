package com.guideaut.project.repo;

import com.guideaut.project.identity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UsuarioRepo extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
}
