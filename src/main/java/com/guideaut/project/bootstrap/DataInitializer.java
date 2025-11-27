package com.guideaut.project.bootstrap;

import com.guideaut.project.identity.*;
import com.guideaut.project.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(
            UsuarioRepo users,
            PapelRepo roles,
            PermissaoRepo perms,
            BCryptPasswordEncoder enc
    ) {
        return args -> {
            // Papel ADMIN
            var adminRole = roles.findByNome("ADMIN").orElseGet(() -> {
                var r = new Papel();
                r.setNome("ADMIN");
                return roles.save(r);
            });

            // Papel USER
            var userRole = roles.findByNome("USER").orElseGet(() -> {
                var r = new Papel();
                r.setNome("USER");
                return roles.save(r);
            });

            // Usuário admin
            if (users.findByEmail("admin@guideaut.com").isEmpty()) {
                var u = new Usuario();
                u.setEmail("admin@guideaut.com");
                u.setNome("Administrador");
                u.setPasswordHash(enc.encode("admin123"));
                u.setStatus(UserStatus.ACTIVE);
                u.getPapeis().add(adminRole);
                users.save(u);
            }

            // Usuário normal 1
            if (users.findByEmail("user1@guideaut.com").isEmpty()) {
                var u = new Usuario();
                u.setEmail("user1@guideaut.com");
                u.setNome("Usuário Padrão 1");
                u.setPasswordHash(enc.encode("user123"));
                u.setStatus(UserStatus.ACTIVE);
                u.getPapeis().add(userRole);
                users.save(u);
            }

            // Usuário normal 2
            if (users.findByEmail("user2@guideaut.com").isEmpty()) {
                var u = new Usuario();
                u.setEmail("user2@guideaut.com");
                u.setNome("Usuário Padrão 2");
                u.setPasswordHash(enc.encode("user123"));
                u.setStatus(UserStatus.ACTIVE);
                u.getPapeis().add(userRole);
                users.save(u);
            }
        };
    }
}
