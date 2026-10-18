package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.HealthInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthInsightRepository extends JpaRepository<HealthInsight, Long> {
    List<HealthInsight> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
