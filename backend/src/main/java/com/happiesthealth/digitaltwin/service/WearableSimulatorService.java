package com.happiesthealth.digitaltwin.service;

import com.happiesthealth.digitaltwin.model.WearableData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class WearableSimulatorService {
    private final WearableService wearableService;
    private final AtomicBoolean isSimulating = new AtomicBoolean(false);
    private final Random random = new Random();

    // Default simulation target is Rahul (patientId: 1)
    private Long activePatientId = 1L;
    private String scenario = "NORMAL"; // NORMAL, HYPERTENSION_SPIKE, SLEEP_DEBT

    public void startSimulation(Long patientId, String scenario) {
        this.activePatientId = (patientId != null) ? patientId : 1L;
        this.scenario = (scenario != null) ? scenario : "NORMAL";
        this.isSimulating.set(true);
        log.info("Wearable simulator STARTED for patient {} under scenario {}", activePatientId, this.scenario);
    }

    public void stopSimulation() {
        this.isSimulating.set(false);
        log.info("Wearable simulator STOPPED");
    }

    public boolean isRunning() {
        return isSimulating.get();
    }

    public String getCurrentScenario() {
        return scenario;
    }

    public List<WearableData> triggerSingleTick(Long patientId, String selectedScenario) {
        Long pId = (patientId != null) ? patientId : activePatientId;
        String sc = (selectedScenario != null) ? selectedScenario : this.scenario;

        LocalDateTime now = LocalDateTime.now();
        List<WearableData> generated = new ArrayList<>();

        double hrBase = 74.0;
        double bpSysBase = 138.0;
        double bpDiaBase = 86.0;

        if ("HYPERTENSION_SPIKE".equalsIgnoreCase(sc)) {
            hrBase = 88.0 + random.nextDouble() * 8.0;
            bpSysBase = 155.0 + random.nextDouble() * 10.0;
            bpDiaBase = 95.0 + random.nextDouble() * 5.0;
        } else if ("SLEEP_DEBT".equalsIgnoreCase(sc)) {
            hrBase = 80.0 + random.nextDouble() * 5.0;
        } else {
            hrBase = 72.0 + (random.nextDouble() * 6.0 - 3.0);
        }

        // 1. Heart Rate
        generated.add(WearableData.builder()
                .patientId(pId)
                .metricType("HEART_RATE")
                .value(Math.round(hrBase * 10.0) / 10.0)
                .unit("BPM")
                .source("SIMULATOR")
                .dataQuality("VALID")
                .recordedAt(now)
                .build());

        // 2. Step increment
        int stepInc = 15 + random.nextInt(40);
        generated.add(WearableData.builder()
                .patientId(pId)
                .metricType("STEPS")
                .value((double) stepInc)
                .unit("COUNT")
                .source("SIMULATOR")
                .dataQuality("VALID")
                .recordedAt(now)
                .build());

        // 3. SpO2
        double spo2 = 97.0 + random.nextInt(3);
        generated.add(WearableData.builder()
                .patientId(pId)
                .metricType("SPO2")
                .value(spo2)
                .unit("%")
                .source("SIMULATOR")
                .dataQuality("VALID")
                .recordedAt(now)
                .build());

        // Ingest into pipeline
        return wearableService.ingestBatch(generated);
    }

    // Auto-tick every 10 seconds if simulation is toggled ON
    @Scheduled(fixedDelay = 10000)
    public void scheduledTick() {
        if (isSimulating.get()) {
            triggerSingleTick(activePatientId, scenario);
        }
    }
}

