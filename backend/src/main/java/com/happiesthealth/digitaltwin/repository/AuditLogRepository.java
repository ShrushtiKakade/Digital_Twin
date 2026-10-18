package com.happiesthealth.digitaltwin.repository;

import com.happiesthealth.digitaltwin.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<AuditLog> findTop50ByOrderByCreatedAtDesc();
}
