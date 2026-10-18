package com.happiesthealth.digitaltwin.service;

import com.happiesthealth.digitaltwin.model.WearableData;
import com.happiesthealth.digitaltwin.repository.WearableDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WearableService {
    private final WearableDataRepository wearableDataRepository;
    private final TwinService twinService;
    private final AuditService auditService;

    public List<WearableData> getLatestReadings(Long patientId, int limit) {
        return wearableDataRepository.findByPatientIdOrderByRecordedAtDesc(patientId, PageRequest.of(0, limit));
    }

    public List<WearableData> getMetricHistory(Long patientId, String metricType, int limit) {
        return wearableDataRepository.findByPatientIdAndMetricTypeOrderByRecordedAtDesc(patientId, metricType, PageRequest.of(0, limit));
    }

    public List<WearableData> getTimelineSince(Long patientId, LocalDateTime since) {
        return wearableDataRepository.findByPatientIdAndRecordedAtAfterOrderByRecordedAtAsc(patientId, since);
    }

    @Transactional
    public WearableData ingestReading(WearableData reading) {
        // 1. Validation & Range Sanity
        validateReading(reading);

        // 2. Persist Raw Immutable Observation
        WearableData saved = wearableDataRepository.save(reading);

        // 3. Trigger Twin Recalculation
        twinService.recalculateTwinState(saved.getPatientId());

        return saved;
    }

    @Transactional
    public List<WearableData> ingestBatch(List<WearableData> readings) {
        if (readings == null || readings.isEmpty()) return Collections.emptyList();

        List<WearableData> valid = new ArrayList<>();
        for (WearableData r : readings) {
            try {
                validateReading(r);
                valid.add(r);
            } catch (Exception e) {
                log.warn("Rejected invalid wearable reading: {}", e.getMessage());
            }
        }

        List<WearableData> savedList = wearableDataRepository.saveAll(valid);
        if (!savedList.isEmpty()) {
            twinService.recalculateTwinState(savedList.get(0).getPatientId());
        }
        return savedList;
    }

    private void validateReading(WearableData reading) {
        if (reading.getPatientId() == null) {
            throw new IllegalArgumentException("Patient ID is required.");
        }
        if (reading.getMetricType() == null || reading.getValue() == null) {
            throw new IllegalArgumentException("Metric type and value are required.");
        }
        if (reading.getRecordedAt() == null) {
            reading.setRecordedAt(LocalDateTime.now());
        }

        // Biological Range Boundary Checks
        String metric = reading.getMetricType().toUpperCase();
        double val = reading.getValue();

        switch (metric) {
            case "HEART_RATE":
                if (val < 30.0 || val > 240.0) {
                    reading.setDataQuality("SUSPICIOUS");
                    auditService.logAction(1L, reading.getPatientId(), "QUALITY_ALERT", "WEARABLE",
                            "Heart rate out of physiological range: " + val + " BPM");
                }
                break;
            case "SPO2":
                if (val < 50.0 || val > 100.0) {
                    reading.setDataQuality("SUSPICIOUS");
                }
                break;
            case "SLEEP_HOURS":
                if (val < 0.0 || val > 24.0) {
                    throw new IllegalArgumentException("Sleep duration cannot exceed 24 hours.");
                }
                break;
            case "STEPS":
                if (val < 0.0 || val > 150000.0) {
                    reading.setDataQuality("SUSPICIOUS");
                }
                break;
        }
    }
}

