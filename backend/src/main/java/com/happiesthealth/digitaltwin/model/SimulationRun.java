package com.happiesthealth.digitaltwin.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulation_runs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 150)
    private String scenarioName;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String hypotheticalInputsJson;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String baselineMetricsJson;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String simulatedMetricsJson;

    @Column(nullable = false)
    private Double baselineRiskScore;

    @Column(nullable = false)
    private Double simulatedRiskScore;

    @Column(nullable = false)
    private Double deltaRiskScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String scientificDisclaimer;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

