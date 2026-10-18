package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.HealthInsight;
import com.happiesthealth.digitaltwin.repository.HealthInsightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/{id}/insights")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InsightController {
    private final HealthInsightRepository healthInsightRepository;

    @GetMapping
    public ResponseEntity<List<HealthInsight>> getInsights(@PathVariable Long id) {
        return ResponseEntity.ok(healthInsightRepository.findByPatientIdOrderByCreatedAtDesc(id));
    }
}
