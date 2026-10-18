package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    Optional<Prediction> findFirstByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<Prediction> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}

