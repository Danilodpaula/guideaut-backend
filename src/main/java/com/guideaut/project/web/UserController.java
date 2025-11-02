package com.guideaut.project.web;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class UserController {
    @Operation(summary = "Dados do usuário autenticado")
    @GetMapping("/me")
    public Object me(Authentication auth) {
        return Map.of("email", auth.getName());
    }

    @Operation(summary = "Endpoint protegido - ADMIN")
    @GetMapping("/admin/users")
    public Object adminOnly() {
        return Map.of("ok", true, "scope", "ADMIN");
    }
}
