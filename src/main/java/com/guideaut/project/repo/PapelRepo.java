package com.guideaut.project.repo;

import com.guideaut.project.identity.Papel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PapelRepo extends JpaRepository<Papel, UUID> {
    Optional<Papel> findByNome(String nome);
}
