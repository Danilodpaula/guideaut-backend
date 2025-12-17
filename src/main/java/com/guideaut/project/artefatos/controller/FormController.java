package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreateFormDto;
import com.guideaut.project.artefatos.dto.FindFormDto;
import com.guideaut.project.artefatos.dto.UpdateFormDto;
import com.guideaut.project.artefatos.service.FormService;
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
@RequestMapping("/form")
@Tag(name = "Roteiros")
public class FormController {

    @NonNull
    private FormService service;

    @NonNull
    private AuditService auditService;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um roteiro pelo ID")
    public ResponseEntity<FindFormDto> findOne(@PathVariable String id, Authentication auth) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id), auth));
    }

    @GetMapping
    @Operation(summary = "Recupera todos os roteiros")
    public ResponseEntity<List<FindFormDto>> findAll(Authentication auth) {
        return ResponseEntity.ok(service.findAll(auth));
    }

    @PostMapping
    @Operation(summary = "Cria um novo roteiro")
    public ResponseEntity<FindFormDto> create(@RequestBody CreateFormDto dto, Authentication auth, HttpServletRequest request) {
        auditService.log("Cria um novo roteiro", auth.getName(), request, Map.of("request", dto), AuditSeverity.INFO);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto, auth));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um roteiro")
    public ResponseEntity<FindFormDto> update(@PathVariable String id, @RequestBody UpdateFormDto dto, Authentication auth, HttpServletRequest request) {
        auditService.log("Atualiza um roteiro", auth.getName(), request, Map.of("id", id, "request", dto), AuditSeverity.INFO);
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto, auth));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um roteiro pelo ID")
    public ResponseEntity<Void> remove(@PathVariable String id, Authentication auth, HttpServletRequest request) {
        auditService.log("Deleta um roteiro pelo ID", auth.getName(), request, Map.of("id", id), AuditSeverity.INFO);
        service.remove(UUID.fromString(id), auth);
        return ResponseEntity.noContent().build();
    }
}
