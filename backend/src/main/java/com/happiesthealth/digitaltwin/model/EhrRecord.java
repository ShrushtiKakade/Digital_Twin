package com.happiesthealth.digitaltwin.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ehr_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EhrRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false, length = 50)
    private String recordType; // LAB_RESULT, DIAGNOSIS, MEDICATION, VITAL_SIGN

    @Column(length = 50)
    private String code; // LOINC or ICD-10

    @Column(nullable = false, length = 255)
    private String description;

    private Double numericValue;

    @Column(length = 50)
    private String unit;

    @Column(columnDefinition = "TEXT")
    private String textValue;

    @Column(length = 255)
    private String sourceFile;

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

