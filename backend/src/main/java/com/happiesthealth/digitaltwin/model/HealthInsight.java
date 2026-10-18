package com.happiesthealth.digitaltwin.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_insights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthInsight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    private Long twinStateId;

    @Column(nullable = false, length = 50)
    private String category; // CARDIOVASCULAR, SLEEP_DEBT, ACTIVITY, MULTI_MODAL

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String severity = "INFO"; // INFO, LOW, MEDIUM, HIGH, WARNING

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "LONGTEXT")
    private String evidenceJson;

    @Column(nullable = false)
    @Builder.Default
    private Double confidenceScore = 0.85;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String dataQualityStatus = "VALID_RECORDS";

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}

