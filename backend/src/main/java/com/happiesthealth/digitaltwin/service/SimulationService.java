package com.happiesthealth.digitaltwin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiesthealth.digitaltwin.model.SimulationRun;
import com.happiesthealth.digitaltwin.model.TwinState;
import com.happiesthealth.digitaltwin.repository.SimulationRunRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationService {
    private final SimulationRunRepository simulationRunRepository;
    private final TwinService twinService;
    private final AIService aiService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SimulationRun> getSimulationsForPatient(Long patientId) {
        return simulationRunRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public Optional<SimulationRun> getSimulationById(Long id) {
        return simulationRunRepository.findById(id);
    }

    @Transactional
    public SimulationRun runWhatIfScenario(Long patientId, String scenarioName, Map<String, Object> hypotheticalInputs) {
        // 1. Fetch current baseline from TwinState
        TwinState currentTwin = twinService.getCurrentTwin(patientId)
                .orElseGet(() -> twinService.recalculateTwinState(patientId));

        Map<String, Object> baselineFeatures = new HashMap<>();
        baselineFeatures.put("patientId", patientId);
        baselineFeatures.put("restingHr", currentTwin.getRestingHrAvg());
        baselineFeatures.put("sleepHours", currentTwin.getSleepDailyAvg());
        baselineFeatures.put("dailySteps", currentTwin.getStepsDailyAvg());
        baselineFeatures.put("systolicBp", currentTwin.getBpSystolicAvg());
        baselineFeatures.put("diastolicBp", currentTwin.getBpDiastolicAvg());

        // 2. Validate input boundaries
        if (hypotheticalInputs.containsKey("sleepHours")) {
            double s = Double.parseDouble(hypotheticalInputs.get("sleepHours").toString());
            if (s < 0.0 || s > 24.0) {
                throw new IllegalArgumentException("Hypothetical sleep hours must be between 0 and 24.");
            }
        }
        if (hypotheticalInputs.containsKey("dailySteps")) {
            double steps = Double.parseDouble(hypotheticalInputs.get("dailySteps").toString());
            if (steps < 0.0 || steps > 100000.0) {
                throw new IllegalArgumentException("Hypothetical daily steps must be between 0 and 100,000.");
            }
        }

        // 3. Delegate to AI Service
        Map<String, Object> simResult = aiService.runSimulation(patientId, scenarioName, baselineFeatures, hypotheticalInputs);

        Map<String, Object> baseRiskMap = (Map<String, Object>) simResult.get("baselineRisk");
        Map<String, Object> simRiskMap = (Map<String, Object>) simResult.get("simulatedRisk");
        Map<String, Object> compMap = (Map<String, Object>) simResult.get("comparison");

        Double baseRisk = ((Number) baseRiskMap.get("estimatedRisk")).doubleValue();
        Double simRisk = ((Number) simRiskMap.get("estimatedRisk")).doubleValue();
        Double deltaRisk = ((Number) compMap.get("absoluteDeltaRisk")).doubleValue();
        String disclaimer = (String) simResult.getOrDefault("scientificDisclaimer", "Research simulation only.");

        String inputsJson = "{}";
        String baselineJson = "{}";
        String simJson = "{}";
        try {
            inputsJson = objectMapper.writeValueAsString(hypotheticalInputs);
            baselineJson = objectMapper.writeValueAsString(simResult.get("baselineFeatures"));
            simJson = objectMapper.writeValueAsString(simResult.get("simulatedFeatures"));
        } catch (Exception ignored) {}

        // 4. Save into simulation_runs (SEPARATE from real observations)
        SimulationRun run = SimulationRun.builder()
                .patientId(patientId)
                .scenarioName(scenarioName != null ? scenarioName : "What-If Clinical Scenario")
                .hypotheticalInputsJson(inputsJson)
                .baselineMetricsJson(baselineJson)
                .simulatedMetricsJson(simJson)
                .baselineRiskScore(baseRisk)
                .simulatedRiskScore(simRisk)
                .deltaRiskScore(deltaRisk)
                .scientificDisclaimer(disclaimer)
                .createdAt(LocalDateTime.now())
                .build();

        SimulationRun saved = simulationRunRepository.save(run);
        auditService.logAction(1L, patientId, "SIMULATE", "SIMULATION_RUN",
                "Executed What-If scenario: " + run.getScenarioName() + ", delta risk: " + deltaRisk);

        return saved;
    }
}
