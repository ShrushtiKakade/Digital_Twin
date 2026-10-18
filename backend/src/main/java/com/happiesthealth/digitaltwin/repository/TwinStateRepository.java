package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.TwinState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TwinStateRepository extends JpaRepository<TwinState, Long> {
    Optional<TwinState> findFirstByPatientIdOrderByCalculatedAtDesc(Long patientId);
    List<TwinState> findByPatientIdOrderByCalculatedAtDesc(Long patientId);
}
