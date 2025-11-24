package com.guideaut.project.artefatos.controller;

import com.guideaut.project.artefatos.dto.CreateEmpathyDto;
import com.guideaut.project.artefatos.dto.FindEmpathyDto;
import com.guideaut.project.artefatos.dto.UpdateEmpathyDto;
import com.guideaut.project.artefatos.service.EmpathyService;
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
public class EmpathyController {

    @NonNull
    private EmpathyService service;

    @GetMapping("/{id}")
    public ResponseEntity<FindEmpathyDto> findOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findOne(id));
    }

    @GetMapping
    public ResponseEntity<List<FindEmpathyDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<FindEmpathyDto> create(@RequestBody CreateEmpathyDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FindEmpathyDto> update(@PathVariable UUID id, @RequestBody UpdateEmpathyDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable UUID id) {
        service.remove(id);
        return ResponseEntity.noContent().build();
    }

}
