package com.guideaut.project.artefatos.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/form")
public class FormController {

    @GetMapping("/{id}")
    public ResponseEntity<String> findOne() {

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"empathy_aut.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body("ok");
    }

    @GetMapping
    public ResponseEntity<String> findAll() {

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"empathy_aut.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body("ok");
    }

    @PostMapping
    public ResponseEntity<Void> create() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update() {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove() {
        return ResponseEntity.ok().build();
    }
}
