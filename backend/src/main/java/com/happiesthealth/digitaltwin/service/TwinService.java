package com.happiesthealth.digitaltwin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiesthealth.digitaltwin.model.*;
import com.happiesthealth.digitaltwin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwinService {
    private final TwinStateRepository twinStateRepository;
    private final WearableDataRepository wearableDataRepository;
    private final EhrRecordRepository ehrRecordRepository;
    private final HealthInsightRepository healthInsightRepository;
    private final PredictionRepository predictionRepository;
    private final AIService aiService;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<TwinState> getCurrentTwin(Long patientId) {
        return twinStateRepository.findFirstByPatientIdOrderByCalculatedAtDesc(patientId);
    }

    public List<TwinState> getTwinHistory(Long patientId) {
        return twinStateRepository.findByPatientIdOrderByCalculatedAtDesc(patientId);
    }

    @Transactional
    public TwinState recalculateTwinState(Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // 1. Calculate Recent Rolling Aggregates
        Double avgHr = wearableDataRepository.calculateAverageSince(patientId, "HEART_RATE", sevenDaysAgo);
        Double avgSteps = wearableDataRepository.calculateAverageSince(patientId, "STEPS", sevenDaysAgo);
        Double avgSleep = wearableDataRepository.calculateAverageSince(patientId, "SLEEP_HOURS", sevenDaysAgo);
        Double avgSpo2 = wearableDataRepository.calculateAverageSince(patientId, "SPO2", sevenDaysAgo);
        Double avgBpSys = wearableDataRepository.calculateAverageSince(patientId, "BLOOD_PRESSURE_SYS", sevenDaysAgo);
        Double avgBpDia = wearableDataRepository.calculateAverageSince(patientId, "BLOOD_PRESSURE_DIA", sevenDaysAgo);

        // Fallbacks from EHR if wearable BP or HR is not yet streamed
        if (avgBpSys == null || avgBpDia == null) {
            List<EhrRecord> vitals = ehrRecordRepository.findByPatientIdAndRecordTypeOrderByRecordedAtDesc(patientId, "VITAL_SIGN");
            for (EhrRecord v : vitals) {
                if (avgBpSys == null && v.getDescription().toLowerCase().contains("systolic")) {
                    avgBpSys = v.getNumericValue();
                }
                if (avgBpDia == null && v.getDescription().toLowerCase().contains("diastolic")) {
                    avgBpDia = v.getNumericValue();
                }
                if (avgHr == null && v.getDescription().toLowerCase().contains("heart rate")) {
                    avgHr = v.getNumericValue();
                }
            }
        }

        // Set realistic clinical defaults if brand new patient
        if (avgHr == null) avgHr = 74.0;
        if (avgSteps == null) avgSteps = 5400.0;
        if (avgSleep == null) avgSleep = 6.4;
        if (avgSpo2 == null) avgSpo2 = 98.0;
        if (avgBpSys == null) avgBpSys = 138.0;
        if (avgBpDia == null) avgBpDia = 86.0;

        // Retrieve last 10 readings for provenance
        List<WearableData> recentReadings = wearableDataRepository.findByPatientIdOrderByRecordedAtDesc(
                patientId, PageRequest.of(0, 15));
        String provenanceIds = recentReadings.stream()
                .map(r -> String.valueOf(r.getId()))
                .collect(Collectors.joining(","));

        // Determine data quality score
        String qualityScore = "OPTIMAL";
        if (recentReadings.isEmpty()) {
            qualityScore = "DEGRADED";
        } else {
            LocalDateTime latestReadingTime = recentReadings.get(0).getRecordedAt();
            if (latestReadingTime.isBefore(now.minusHours(12))) {
                qualityScore = "STALE";
            }
        }

        Map<String, Object> snapshotMap = new HashMap<>();
        snapshotMap.put("restingHr", avgHr);
        snapshotMap.put("dailySteps", avgSteps);
        snapshotMap.put("sleepHours", avgSleep);
        snapshotMap.put("spo2", avgSpo2);
        snapshotMap.put("systolicBp", avgBpSys);
        snapshotMap.put("diastolicBp", avgBpDia);
        snapshotMap.put("calculatedAt", now.toString());

        String snapshotJson = "{}";
        try {
            snapshotJson = objectMapper.writeValueAsString(snapshotMap);
        } catch (Exception ignored) {}

        TwinState state = TwinState.builder()
                .patientId(patientId)
                .restingHrAvg(Math.round(avgHr * 10.0) / 10.0)
                .stepsDailyAvg(Math.round(avgSteps * 1.0) / 1.0)
                .sleepDailyAvg(Math.round(avgSleep * 10.0) / 10.0)
                .spo2Avg(Math.round(avgSpo2 * 10.0) / 10.0)
                .bpSystolicAvg(Math.round(avgBpSys * 10.0) / 10.0)
                .bpDiastolicAvg(Math.round(avgBpDia * 10.0) / 10.0)
                .dataQualityScore(qualityScore)
                .provenanceRecordIds(provenanceIds)
                .metricsSnapshotJson(snapshotJson)
                .calculatedAt(now)
                .build();

        TwinState savedTwin = twinStateRepository.save(state);
        log.info("Updated TwinState for patient {}: HR={}, Sleep={}, BP={}/{}, Quality={}",
                patientId, state.getRestingHrAvg(), state.getSleepDailyAvg(), state.getBpSystolicAvg(), state.getBpDiastolicAvg(), qualityScore);

        // 2. Trigger Asynchronous/Decoupled AI Pipeline
        triggerAIPipeline(patientId, savedTwin, recentReadings);

        return savedTwin;
    }

    private void triggerAIPipeline(Long patientId, TwinState twin, List<WearableData> recentReadings) {
        try {
            Map<String, Object> baseline = new HashMap<>();
            baseline.put("restingHrAvg", twin.getRestingHrAvg());
            baseline.put("sleepDailyAvg", twin.getSleepDailyAvg());
            baseline.put("stepsDailyAvg", twin.getStepsDailyAvg());

            List<Map<String, Object>> historyMaps = new ArrayList<>();
            for (WearableData w : recentReadings) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", w.getId());
                m.put("metricType", w.getMetricType());
                m.put("value", w.getValue());
                m.put("unit", w.getUnit());
                m.put("recordedAt", w.getRecordedAt().toString());
                historyMaps.add(m);
            }

            // Insights
            List<Map<String, Object>> aiInsights = aiService.generateInsights(patientId, baseline, historyMaps);
            for (Map<String, Object> ins : aiInsights) {
                HealthInsight hi = HealthInsight.builder()
                        .patientId(patientId)
                        .twinStateId(twin.getId())
                        .category((String) ins.getOrDefault("category", "GENERAL"))
                        .severity((String) ins.getOrDefault("severity", "INFO"))
                        .title((String) ins.getOrDefault("title", "Insight"))
                        .explanation((String) ins.getOrDefault("explanation", ""))
                        .confidenceScore(((Number) ins.getOrDefault("confidenceScore", 0.85)).doubleValue())
                        .dataQualityStatus((String) ins.getOrDefault("dataQualityStatus", "VALID_RECORDS"))
                        .evidenceJson(objectMapper.writeValueAsString(ins.getOrDefault("evidence", Collections.emptyMap())))
                        .build();
                healthInsightRepository.save(hi);
            }

            // Predictive Risk
            Map<String, Object> predFeatures = new HashMap<>();
            predFeatures.put("patientId", patientId);
            predFeatures.put("systolicBp", twin.getBpSystolicAvg());
            predFeatures.put("diastolicBp", twin.getBpDiastolicAvg());
            predFeatures.put("restingHr", twin.getRestingHrAvg());
            predFeatures.put("sleepHours", twin.getSleepDailyAvg());
            predFeatures.put("dailySteps", twin.getStepsDailyAvg());

            Map<String, Object> predResult = aiService.generatePrediction(predFeatures);
            if (predResult.containsKey("estimatedRisk")) {
                Map<String, Object> ci = (Map<String, Object>) predResult.get("confidenceInterval");
                Prediction p = Prediction.builder()
                        .patientId(patientId)
                        .twinStateId(twin.getId())
                        .outcomeType((String) predResult.getOrDefault("outcomeType", "30_DAY_CARDIOVASCULAR_EXACERBATION_RISK"))
                        .predictionHorizonDays(30)
                        .estimatedRisk(((Number) predResult.get("estimatedRisk")).doubleValue())
                        .confidenceLower(ci != null && ci.containsKey("lower") ? ((Number) ci.get("lower")).doubleValue() : null)
                        .confidenceUpper(ci != null && ci.containsKey("upper") ? ((Number) ci.get("upper")).doubleValue() : null)
                        .modelVersion((String) predResult.getOrDefault("modelVersion", "cardio-twin-rf-v1.2"))
                        .dataQualityStatus((String) predResult.getOrDefault("dataQualityStatus", "ACCEPTABLE"))
                        .featureAttributionsJson(objectMapper.writeValueAsString(predResult.getOrDefault("featureAttributions", Collections.emptyMap())))
                        .build();
                predictionRepository.save(p);
            }
        } catch (Exception e) {
            log.error("AI pipeline trigger exception for patient {}: {}", patientId, e.getMessage());
        }
    }
}

