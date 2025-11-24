package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreatePersonaDto;
import com.guideaut.project.artefatos.dto.FindPersonaDto;
import com.guideaut.project.artefatos.dto.UpdatePersonaDto;
import com.guideaut.project.artefatos.service.PersonaService;
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
public class PersonaController {

    @NonNull
    private PersonaService service;

    @GetMapping("/{id}")
    public ResponseEntity<FindPersonaDto> findOne(@PathVariable String id) {
        return ResponseEntity.ok(service.findOne(UUID.fromString(id)));
    }

    @GetMapping
    public ResponseEntity<List<FindPersonaDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<FindPersonaDto> create(@RequestBody CreatePersonaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FindPersonaDto> update(@PathVariable String id, @RequestBody UpdatePersonaDto dto) {
        return ResponseEntity.ok(service.update(UUID.fromString(id), dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable String id) {
        service.remove(UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
