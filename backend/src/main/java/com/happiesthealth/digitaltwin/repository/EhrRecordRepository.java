package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.EhrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EhrRecordRepository extends JpaRepository<EhrRecord, Long> {
    List<EhrRecord> findByPatientIdOrderByRecordedAtDesc(Long patientId);
    List<EhrRecord> findByPatientIdAndRecordTypeOrderByRecordedAtDesc(Long patientId, String recordType);
}

