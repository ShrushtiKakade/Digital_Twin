package com.happiesthealth.digitaltwin.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wearable_data", indexes = {
    @Index(name = "idx_patient_metric_time", columnList = "patientId, metricType, recordedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WearableData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 50)
    private String metricType; // HEART_RATE, STEPS, SPO2, SLEEP_HOURS, BLOOD_PRESSURE_SYS, BLOOD_PRESSURE_DIA

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false, length = 100)
    @Builder.Default
    private String source = "SIMULATOR";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String dataQuality = "VALID"; // VALID, SUSPICIOUS, STALE, INTERPOLATED

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (recordedAt == null) recordedAt = LocalDateTime.now();
    }
}

