package com.guideaut.project.repo;

import com.guideaut.project.identity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PermissaoRepo extends JpaRepository<Permissao, UUID> {
    Optional<Permissao> findByNome(String nome);
}
