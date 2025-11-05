package com.proaula.fitnesslife.controller;

import com.proaula.fitnesslife.model.FunctionalTraining;
import com.proaula.fitnesslife.service.FunctionalTrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class FunctionalTrainingController {

    private final FunctionalTrainingService service;

    public FunctionalTrainingController(FunctionalTrainingService service) {
        this.service = service;
    }

    @GetMapping
    public List<FunctionalTraining> getAllTrainings() {
        return service.getAllTrainings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunctionalTraining> getTrainingById(@PathVariable String id) {
        return service.getTrainingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FunctionalTraining> createTraining(@RequestBody FunctionalTraining training) {
        try {
            FunctionalTraining created = service.createTraining(training);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionalTraining> updateTraining(@PathVariable String id, @RequestBody FunctionalTraining training) {
        try {
            FunctionalTraining updated = service.updateTraining(id, training);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable String id) {
        service.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }
}
