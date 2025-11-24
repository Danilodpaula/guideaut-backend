package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreateFormDto;
import com.guideaut.project.artefatos.dto.FindFormDto;
import com.guideaut.project.artefatos.dto.UpdateFormDto;
import com.guideaut.project.artefatos.service.FormService;
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
@RequestMapping("/form")
@Tag(name = "Roteiros")
public class FormController {

    @NonNull
    private FormService service;

    @GetMapping("/{id}")
    @Operation(summary = "Recupera um roteiro pelo ID")
    public ResponseEntity<FindFormDto> findOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id)));
    }

    @GetMapping
    @Operation(summary = "Recupera todos os roteiros")
    public ResponseEntity<List<FindFormDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @Operation(summary = "Cria um novo roteiro")
    public ResponseEntity<FindFormDto> create(@RequestBody CreateFormDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um roteiro")
    public ResponseEntity<FindFormDto> update(@PathVariable String id, @RequestBody UpdateFormDto dto) {
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um roteiro pelo ID")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        service.remove(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
