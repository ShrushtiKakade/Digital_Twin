package com.happiesthealth.digitaltwin.service;

import com.happiesthealth.digitaltwin.model.AuditLog;
import com.happiesthealth.digitaltwin.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public void logAction(Long userId, Long patientId, String action, String resource, String details) {
        AuditLog audit = AuditLog.builder()
                .userId(userId)
                .patientId(patientId)
                .action(action)
                .resource(resource)
                .details(details)
                .build();
        auditLogRepository.save(audit);
        log.info("[AUDIT] Action: {}, Resource: {}, Patient: {}", action, resource, patientId);
    }

    public List<AuditLog> getLogsForPatient(Long patientId) {
        return auditLogRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }
}
