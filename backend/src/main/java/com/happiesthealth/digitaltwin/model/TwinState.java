package com.happiesthealth.digitaltwin.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "twin_states", indexes = {
    @Index(name = "idx_twin_patient_time", columnList = "patientId, calculatedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwinState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Double restingHrAvg;
    private Double stepsDailyAvg;
    private Double sleepDailyAvg;
    private Double spo2Avg;
    private Double bpSystolicAvg;
    private Double bpDiastolicAvg;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String dataQualityScore = "ACCEPTABLE"; // OPTIMAL, ACCEPTABLE, DEGRADED, STALE

    @Column(columnDefinition = "LONGTEXT")
    private String metricsSnapshotJson;

    @Column(columnDefinition = "TEXT")
    private String provenanceRecordIds;

    @Column(updatable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        if (calculatedAt == null) calculatedAt = LocalDateTime.now();
    }
}

