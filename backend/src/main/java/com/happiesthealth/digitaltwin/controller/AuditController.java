package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.AuditLog;
import com.happiesthealth.digitaltwin.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AuditLog>> getPatientAuditLogs(@PathVariable Long patientId) {
        return ResponseEntity.ok(auditService.getLogsForPatient(patientId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<AuditLog>> getRecentAuditLogs() {
        return ResponseEntity.ok(auditService.getRecentLogs());
    }
}

