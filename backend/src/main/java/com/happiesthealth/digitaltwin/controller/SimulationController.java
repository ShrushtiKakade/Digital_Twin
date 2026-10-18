package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.SimulationRun;
import com.happiesthealth.digitaltwin.service.SimulationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimulationController {
    private final SimulationService simulationService;

    @Data
    public static class SimulationRequestDto {
        private Long patientId;
        private String scenarioName;
        private Map<String, Object> hypotheticalInputs;
    }

    @PostMapping
    public ResponseEntity<SimulationRun> executeSimulation(@RequestBody SimulationRequestDto dto) {
        if (dto.getPatientId() == null) {
            return ResponseEntity.badRequest().build();
        }
        SimulationRun run = simulationService.runWhatIfScenario(
                dto.getPatientId(),
                dto.getScenarioName(),
                dto.getHypotheticalInputs()
        );
        return ResponseEntity.ok(run);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimulationRun> getSimulationById(@PathVariable Long id) {
        return simulationService.getSimulationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SimulationRun>> getPatientSimulations(@PathVariable Long patientId) {
        return ResponseEntity.ok(simulationService.getSimulationsForPatient(patientId));
    }
}
