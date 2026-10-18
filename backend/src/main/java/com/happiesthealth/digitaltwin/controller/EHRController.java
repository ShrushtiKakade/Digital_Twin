package com.happiesthealth.digitaltwin.controller;

import com.happiesthealth.digitaltwin.model.EhrRecord;
import com.happiesthealth.digitaltwin.service.EHRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ehr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EHRController {
    private final EHRService ehrService;

    @GetMapping("/{patientId}")
    public ResponseEntity<List<EhrRecord>> getPatientEhr(@PathVariable Long patientId) {
        return ResponseEntity.ok(ehrService.getRecordsForPatient(patientId));
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importEhr(
            @RequestParam("patientId") Long patientId,
            @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> report = ehrService.importEhrCsv(patientId, file);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "EHR Import failed: " + e.getMessage()));
        }
    }
}

