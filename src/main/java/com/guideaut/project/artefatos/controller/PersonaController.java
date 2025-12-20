package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreatePersonaDto;
import com.guideaut.project.artefatos.dto.FindPersonaDto;
import com.guideaut.project.artefatos.dto.UpdatePersonaDto;
import com.guideaut.project.artefatos.service.PersonaService;
import com.guideaut.project.audit.AuditService;
import com.guideaut.project.audit.AuditSeverity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persona")
@Tag(name = "Personas")
public class PersonaController {

    @NonNull
    private PersonaService service;

    @NonNull
    private AuditService auditService;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera uma persona pelo ID")
    public ResponseEntity<FindPersonaDto> findOne(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id), auth));
    }

    @GetMapping
    @Operation(summary = "Recupera todas as personas")
    public ResponseEntity<List<FindPersonaDto>> findAll(Authentication auth) {
        return ResponseEntity.ok(service.findAll(auth));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova persona")
    public ResponseEntity<FindPersonaDto> create(@RequestBody CreatePersonaDto dto, Authentication auth, HttpServletRequest request) {
        auditService.log("Cria uma nova persona", auth.getName(), request, Map.of("request", dto), AuditSeverity.INFO);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma persona")
    public ResponseEntity<FindPersonaDto> update(@PathVariable String id, @RequestBody UpdatePersonaDto dto, Authentication auth, HttpServletRequest request) {
        auditService.log("Atualiza uma persona", auth.getName(), request, Map.of("id", id, "request", dto), AuditSeverity.INFO);
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto, auth));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma persona pelo ID")
    public ResponseEntity<Void> remove(@PathVariable String id, Authentication auth, HttpServletRequest request) {
        auditService.log("Deleta uma persona pelo ID", auth.getName(), request, Map.of("id", id), AuditSeverity.INFO);
        service.remove(UUID.fromString(id), auth);
        return ResponseEntity.noContent().build();
    }
}
