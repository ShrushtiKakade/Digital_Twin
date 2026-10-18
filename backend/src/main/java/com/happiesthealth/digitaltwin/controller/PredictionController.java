package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.Prediction;
import com.happiesthealth.digitaltwin.model.TwinState;
import com.happiesthealth.digitaltwin.repository.PredictionRepository;
import com.happiesthealth.digitaltwin.service.AIService;
import com.happiesthealth.digitaltwin.service.TwinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/patients/{id}/predictions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PredictionController {
    private final PredictionRepository predictionRepository;
    private final TwinService twinService;
    private final AIService aiService;

    @GetMapping
    public ResponseEntity<List<Prediction>> getPredictionHistory(@PathVariable Long id) {
        return ResponseEntity.ok(predictionRepository.findByPatientIdOrderByCreatedAtDesc(id));
    }

    @GetMapping("/latest")
    public ResponseEntity<Prediction> getLatestPrediction(@PathVariable Long id) {
        return predictionRepository.findFirstByPatientIdOrderByCreatedAtDesc(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<Prediction> triggerPrediction(@PathVariable Long id) {
        TwinState twin = twinService.getCurrentTwin(id)
                .orElseGet(() -> twinService.recalculateTwinState(id));

        Map<String, Object> features = new HashMap<>();
        features.put("patientId", id);
        features.put("systolicBp", twin.getBpSystolicAvg());
        features.put("diastolicBp", twin.getBpDiastolicAvg());
        features.put("restingHr", twin.getRestingHrAvg());
        features.put("sleepHours", twin.getSleepDailyAvg());
        features.put("dailySteps", twin.getStepsDailyAvg());

        Map<String, Object> result = aiService.generatePrediction(features);
        Double risk = result.containsKey("estimatedRisk") ? ((Number) result.get("estimatedRisk")).doubleValue() : 0.08;
        String modelVer = (String) result.getOrDefault("modelVersion", "cardio-twin-rf-v1.2");

        Prediction p = Prediction.builder()
                .patientId(id)
                .twinStateId(twin.getId())
                .outcomeType("30_DAY_CARDIOVASCULAR_EXACERBATION_RISK")
                .predictionHorizonDays(30)
                .estimatedRisk(risk)
                .modelVersion(modelVer)
                .dataQualityStatus((String) result.getOrDefault("dataQualityStatus", "ACCEPTABLE"))
                .build();

        return ResponseEntity.ok(predictionRepository.save(p));
    }
}

