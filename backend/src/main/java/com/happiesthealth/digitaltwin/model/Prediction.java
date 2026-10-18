package com.happiesthealth.digitaltwin.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Long twinStateId;

    @Column(nullable = false, length = 100)
    private String outcomeType; // 30_DAY_CARDIOVASCULAR_EXACERBATION_RISK

    @Column(nullable = false)
    @Builder.Default
    private Integer predictionHorizonDays = 30;

    @Column(nullable = false)
    private Double estimatedRisk;

    private Double confidenceLower;
    private Double confidenceUpper;

    @Column(nullable = false, length = 50)
    private String modelVersion;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String dataQualityStatus = "ACCEPTABLE";

    @Column(columnDefinition = "LONGTEXT")
    private String featureAttributionsJson;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

