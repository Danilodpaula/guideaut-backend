package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreateEmpathyDto;
import com.guideaut.project.artefatos.dto.FindEmpathyDto;
import com.guideaut.project.artefatos.dto.UpdateEmpathyDto;
import com.guideaut.project.artefatos.service.EmpathyService;
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
@RequestMapping("/empathy")
@Tag(name = "Mapas de Empatia")
public class EmpathyController {

    @NonNull
    private EmpathyService service;

    @NonNull
    private AuditService auditService;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um mapa de empatia pelo ID")
    public ResponseEntity<FindEmpathyDto> findOne(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id), auth));
    }

    @GetMapping
    @Operation(summary = "Recupera todos os mapa de empatia")
    public ResponseEntity<List<FindEmpathyDto>> findAll(Authentication auth) {
        return ResponseEntity.ok(service.findAll(auth));
    }

    @PostMapping
    @Operation(summary = "Cria um novo mapa de empatia")
    public ResponseEntity<FindEmpathyDto> create(@RequestBody CreateEmpathyDto dto, Authentication auth, HttpServletRequest request) {
        auditService.log("Cria um novo mapa de empatia", auth.getName(), request, Map.of("request", dto), AuditSeverity.INFO);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um mapa de empatia")
    public ResponseEntity<FindEmpathyDto> update(@PathVariable String id, @RequestBody UpdateEmpathyDto dto, Authentication auth, HttpServletRequest request) {
        auditService.log("Atualiza um mapa de empatia", auth.getName(), request, Map.of("id", id, "request", dto), AuditSeverity.INFO);
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto, auth));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um mapa de empatia pelo ID")
    public ResponseEntity<Void> remove(@PathVariable String id, Authentication auth, HttpServletRequest request) {
        auditService.log("Deleta um mapa de empatia pelo ID", auth.getName(), request, Map.of("id", id), AuditSeverity.INFO);
        service.remove(UUID.fromString(id), auth);
        return ResponseEntity.noContent().build();
    }

}
