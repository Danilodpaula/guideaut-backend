package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreateEmpathyDto;
import com.guideaut.project.artefatos.dto.FindEmpathyDto;
import com.guideaut.project.artefatos.dto.UpdateEmpathyDto;
import com.guideaut.project.artefatos.service.EmpathyService;
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
@RequestMapping("/empathy")
@Tag(name = "Mapas de Empatia")
public class EmpathyController {

    @NonNull
    private EmpathyService service;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um mapa de empatia pelo ID")
    public ResponseEntity<FindEmpathyDto> findOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id)));
    }

    @GetMapping
    @Operation(summary = "Recupera todos os mapa de empatia")
    public ResponseEntity<List<FindEmpathyDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @Operation(summary = "Cria um novo mapa de empatia")
    public ResponseEntity<FindEmpathyDto> create(@RequestBody CreateEmpathyDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um mapa de empatia")
    public ResponseEntity<FindEmpathyDto> update(@PathVariable String id, @RequestBody UpdateEmpathyDto dto) {
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um mapa de empatia pelo ID")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        service.remove(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }

}
