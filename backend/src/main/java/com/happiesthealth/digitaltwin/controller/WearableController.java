package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.WearableData;
import com.happiesthealth.digitaltwin.service.WearableService;
import com.happiesthealth.digitaltwin.service.WearableSimulatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/wearables")
@RequiredArgsConstructor
public class WearableController {
    private final WearableService wearableService;
    private final WearableSimulatorService simulatorService;

    @PostMapping("/readings")
    public ResponseEntity<WearableData> ingestReading(@RequestBody WearableData reading) {
        return ResponseEntity.ok(wearableService.ingestReading(reading));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<WearableData>> ingestBatch(@RequestBody List<WearableData> readings) {
        return ResponseEntity.ok(wearableService.ingestBatch(readings));
    }

    @GetMapping("/{patientId}/latest")
    public ResponseEntity<List<WearableData>> getLatestReadings(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(wearableService.getLatestReadings(patientId, limit));
    }

    @GetMapping("/{patientId}/metric/{metricType}")
    public ResponseEntity<List<WearableData>> getMetricHistory(
            @PathVariable Long patientId,
            @PathVariable String metricType,
            @RequestParam(defaultValue = "30") int limit) {
        return ResponseEntity.ok(wearableService.getMetricHistory(patientId, metricType, limit));
    }

    // --- Simulator Endpoints ---
    @PostMapping("/simulator/start")
    public ResponseEntity<Map<String, Object>> startSimulator(
            @RequestParam(required = false, defaultValue = "1") Long patientId,
            @RequestParam(required = false, defaultValue = "NORMAL") String scenario) {
        simulatorService.startSimulation(patientId, scenario);
        return ResponseEntity.ok(Map.of(
                "status", "RUNNING",
                "patientId", patientId,
                "scenario", scenario,
                "message", "Wearable telemetry simulator is running."
        ));
    }

    @PostMapping("/simulator/stop")
    public ResponseEntity<Map<String, Object>> stopSimulator() {
        simulatorService.stopSimulation();
        return ResponseEntity.ok(Map.of("status", "STOPPED", "message", "Simulator stopped."));
    }

    @GetMapping("/simulator/status")
    public ResponseEntity<Map<String, Object>> getSimulatorStatus() {
        return ResponseEntity.ok(Map.of(
                "running", simulatorService.isRunning(),
                "scenario", simulatorService.getCurrentScenario()
        ));
    }

    @PostMapping("/simulator/tick")
    public ResponseEntity<List<WearableData>> triggerTick(
            @RequestParam(required = false, defaultValue = "1") Long patientId,
            @RequestParam(required = false, defaultValue = "NORMAL") String scenario) {
        List<WearableData> generated = simulatorService.triggerSingleTick(patientId, scenario);
        return ResponseEntity.ok(generated);
    }
}

