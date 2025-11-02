package com.guideaut.project.bootstrap;

import com.guideaut.project.identity.*;
import com.guideaut.project.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner init(UsuarioRepo users, PapelRepo roles, PermissaoRepo perms, BCryptPasswordEncoder enc) {
        return args -> {
            var adminRole = roles.findByNome("ADMIN").orElseGet(() -> {
                var r = new Papel();
                r.setNome("ADMIN");
                return roles.save(r);
            });
            if (users.findByEmail("admin@guideaut.com").isEmpty()) {
                var u = new Usuario();
                u.setEmail("admin@guideaut.com");
                u.setNome("Administrador");
                u.setPasswordHash(enc.encode("admin123"));
                u.setStatus(UserStatus.ACTIVE);
                u.getPapeis().add(adminRole);
                users.save(u);
            }
        };
    }
}
