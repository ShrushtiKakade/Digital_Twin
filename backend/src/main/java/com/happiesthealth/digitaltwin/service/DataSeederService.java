package com.happiesthealth.digitaltwin.service;

import com.happiesthealth.digitaltwin.model.EhrRecord;
import com.happiesthealth.digitaltwin.model.Patient;
import com.happiesthealth.digitaltwin.model.WearableData;
import com.happiesthealth.digitaltwin.repository.EhrRecordRepository;
import com.happiesthealth.digitaltwin.repository.PatientRepository;
import com.happiesthealth.digitaltwin.repository.WearableDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeederService implements CommandLineRunner {
    private final PatientRepository patientRepository;
    private final EhrRecordRepository ehrRecordRepository;
    private final WearableDataRepository wearableDataRepository;
    private final TwinService twinService;

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            log.info("Database already seeded with patient data.");
            return;
        }

        log.info("Seeding initial synthetic patient Rahul Verma and baseline health history...");

        // 1. Patient Profile
        Patient rahul = Patient.builder()
                .mrn("MRN-90214")
                .fullName("Rahul Verma")
                .dateOfBirth(LocalDate.of(1974, 6, 15))
                .gender("MALE")
                .primaryCondition("Essential Hypertension (ICD-10 I10)")
                .contactPhone("+91 98765 43210")
                .contactEmail("rahul.verma@example.com")
                .build();
        rahul = patientRepository.save(rahul);

        // 2. EHR Records
        List<EhrRecord> ehrList = new ArrayList<>();
        LocalDateTime baseDate = LocalDateTime.now().minusDays(30);

        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("DIAGNOSIS").code("I10")
                .description("Essential (primary) hypertension").textValue("Diagnosed stage 2 hypertension").recordedAt(baseDate).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("DIAGNOSIS").code("E78.0")
                .description("Pure Hypercholesterolemia").textValue("Borderline elevated LDL").recordedAt(baseDate).build());

        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("MEDICATION").code("RX-AML")
                .description("Amlodipine Besylate").numericValue(5.0).unit("mg").textValue("Oral, 1 tablet once daily in morning").recordedAt(baseDate).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("MEDICATION").code("RX-ATV")
                .description("Atorvastatin Calcium").numericValue(10.0).unit("mg").textValue("Oral, 1 tablet at bedtime").recordedAt(baseDate).build());

        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("LAB_RESULT").code("2160-0")
                .description("Serum Creatinine").numericValue(1.05).unit("mg/dL").textValue("Normal").recordedAt(baseDate.plusDays(5)).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("LAB_RESULT").code("1558-6")
                .description("Fasting Blood Glucose").numericValue(104.0).unit("mg/dL").textValue("Mildly elevated").recordedAt(baseDate.plusDays(5)).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("LAB_RESULT").code("4548-4")
                .description("Hemoglobin A1c").numericValue(5.8).unit("%").textValue("Prediabetes threshold").recordedAt(baseDate.plusDays(5)).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("LAB_RESULT").code("2093-3")
                .description("Total Cholesterol").numericValue(215.0).unit("mg/dL").textValue("Borderline high").recordedAt(baseDate.plusDays(5)).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("LAB_RESULT").code("13457-7")
                .description("LDL Cholesterol").numericValue(138.0).unit("mg/dL").textValue("Elevated").recordedAt(baseDate.plusDays(5)).build());

        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("VITAL_SIGN").code("8480-6")
                .description("Systolic Blood Pressure").numericValue(142.0).unit("mmHg").recordedAt(baseDate.plusDays(10)).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("VITAL_SIGN").code("8462-4")
                .description("Diastolic Blood Pressure").numericValue(88.0).unit("mmHg").recordedAt(baseDate.plusDays(10)).build());
        ehrList.add(EhrRecord.builder().patientId(rahul.getId()).recordType("VITAL_SIGN").code("8867-4")
                .description("Resting Heart Rate").numericValue(74.0).unit("BPM").recordedAt(baseDate.plusDays(10)).build());

        ehrRecordRepository.saveAll(ehrList);

        // 3. 14 Days of Wearable History (illustrating past week healthy vs recent sleep drop & elevated HR)
        List<WearableData> wearableList = new ArrayList<>();
        for (int day = 14; day >= 1; day--) {
            LocalDateTime dayTime = LocalDateTime.now().minusDays(day).withHour(8).withMinute(0);

            // Previous week: Sleep ~ 7.2h, HR ~ 72 BPM. Recent week: Sleep ~ 6.1h, HR ~ 78 BPM.
            double sleep = (day > 7) ? (7.1 + (day % 3) * 0.2) : (6.1 - (day % 2) * 0.3);
            double hr = (day > 7) ? (71.0 + (day % 4)) : (77.0 + (day % 5));
            double steps = (day > 7) ? (6800.0 - day * 50) : (4900.0 + day * 40);
            double sysBp = (day > 7) ? 134.0 : 144.0;
            double diaBp = (day > 7) ? 84.0 : 89.0;

            wearableList.add(WearableData.builder().patientId(rahul.getId()).metricType("SLEEP_HOURS")
                    .value(Math.round(sleep * 10.0) / 10.0).unit("HOURS").source("WEARABLE_DEVICE").dataQuality("VALID").recordedAt(dayTime).build());
            wearableList.add(WearableData.builder().patientId(rahul.getId()).metricType("HEART_RATE")
                    .value(hr).unit("BPM").source("WEARABLE_DEVICE").dataQuality("VALID").recordedAt(dayTime.withHour(14)).build());
            wearableList.add(WearableData.builder().patientId(rahul.getId()).metricType("STEPS")
                    .value(steps).unit("COUNT").source("WEARABLE_DEVICE").dataQuality("VALID").recordedAt(dayTime.withHour(20)).build());
            wearableList.add(WearableData.builder().patientId(rahul.getId()).metricType("BLOOD_PRESSURE_SYS")
                    .value(sysBp).unit("mmHg").source("CONNECTED_CUFF").dataQuality("VALID").recordedAt(dayTime.withHour(9)).build());
            wearableList.add(WearableData.builder().patientId(rahul.getId()).metricType("BLOOD_PRESSURE_DIA")
                    .value(diaBp).unit("mmHg").source("CONNECTED_CUFF").dataQuality("VALID").recordedAt(dayTime.withHour(9)).build());
            wearableList.add(WearableData.builder().patientId(rahul.getId()).metricType("SPO2")
                    .value(98.0).unit("%").source("WEARABLE_DEVICE").dataQuality("VALID").recordedAt(dayTime.withHour(14)).build());
        }
        wearableDataRepository.saveAll(wearableList);

        // 4. Initial Twin Calculation
        twinService.recalculateTwinState(rahul.getId());

        log.info("Patient Rahul Verma successfully seeded with full EHR, 14-day wearable timeline, and Digital Twin state!");
    }
}

