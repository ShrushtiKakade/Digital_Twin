package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.SimulationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulationRunRepository extends JpaRepository<SimulationRun, Long> {
    List<SimulationRun> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
