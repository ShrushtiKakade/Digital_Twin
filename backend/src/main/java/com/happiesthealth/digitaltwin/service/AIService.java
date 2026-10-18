package com.happiesthealth.digitaltwin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class AIService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai-service.url:http://localhost:8000}")
    private String aiServiceUrl;

    public boolean isAiServiceAvailable() {
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(aiServiceUrl + "/health", Map.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("AI Microservice unreachable at {}: {}", aiServiceUrl, e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> generateInsights(Long patientId, Map<String, Object> baseline, List<Map<String, Object>> wearableHistory) {
        if (!isAiServiceAvailable()) {
            log.warn("AI Service offline. Returning fallback insight for patient {}", patientId);
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("patientId", patientId);
            fallback.put("category", "SYSTEM");
            fallback.put("severity", "INFO");
            fallback.put("title", "AI Analytics Microservice Offline");
            fallback.put("explanation", "Predictive and insight engines are running in decoupled asynchronous mode.");
            fallback.put("confidenceScore", 1.0);
            fallback.put("dataQualityStatus", "SERVICE_OFFLINE");
            return Collections.singletonList(fallback);
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("patientId", patientId);
            payload.put("baseline", baseline);
            payload.put("wearableHistory", wearableHistory);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiServiceUrl + "/analytics/insights", entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (List<Map<String, Object>>) response.getBody().get("insights");
            }
        } catch (Exception e) {
            log.error("Error invoking AI Insights service: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public Map<String, Object> generatePrediction(Map<String, Object> features) {
        if (!isAiServiceAvailable()) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("outcomeType", "30_DAY_CARDIOVASCULAR_EXACERBATION_RISK");
            fallback.put("predictionHorizonDays", 30);
            fallback.put("estimatedRisk", 0.0);
            fallback.put("riskPercentage", 0.0);
            fallback.put("riskTier", "UNKNOWN");
            fallback.put("modelVersion", "offline-stub");
            fallback.put("dataQualityStatus", "AI_SERVICE_OFFLINE");
            fallback.put("clinicalDisclaimer", "Prediction engine currently unreachable. Raw telemetry preserved safely in MySQL.");
            return fallback;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(features, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiServiceUrl + "/analytics/predict", entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Error predicting cardio risk: {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    public Map<String, Object> runSimulation(Long patientId, String scenarioName, Map<String, Object> baseline, Map<String, Object> overrides) {
        if (!isAiServiceAvailable()) {
            throw new IllegalStateException("AI Service must be running at " + aiServiceUrl + " to execute What-If simulations.");
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("patientId", patientId);
            payload.put("scenarioName", scenarioName);
            payload.put("baselineFeatures", baseline);
            payload.put("hypotheticalInputs", overrides);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiServiceUrl + "/analytics/simulate", entity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Simulation error: {}", e.getMessage());
            throw new RuntimeException("Simulation execution failed: " + e.getMessage());
        }
    }
}
