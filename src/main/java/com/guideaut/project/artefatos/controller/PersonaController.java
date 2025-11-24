package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreatePersonaDto;
import com.guideaut.project.artefatos.dto.FindPersonaDto;
import com.guideaut.project.artefatos.dto.UpdatePersonaDto;
import com.guideaut.project.artefatos.service.PersonaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/persona")
@Tag(name = "Personas")
public class PersonaController {

    @NonNull
    private PersonaService service;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera uma persona pelo ID")
    public ResponseEntity<FindPersonaDto> findOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id)));
    }

    @GetMapping
    @Operation(summary = "Recupera todas as personas")
    public ResponseEntity<List<FindPersonaDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @Operation(summary = "Cria uma nova persona")
    public ResponseEntity<FindPersonaDto> create(@RequestBody CreatePersonaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma persona")
    public ResponseEntity<FindPersonaDto> update(@PathVariable String id, @RequestBody UpdatePersonaDto dto) {
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma persona pelo ID")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        service.remove(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
