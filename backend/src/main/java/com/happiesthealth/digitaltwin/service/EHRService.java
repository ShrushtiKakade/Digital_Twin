package com.happiesthealth.digitaltwin.service;

import com.happiesthealth.digitaltwin.model.EhrRecord;
import com.happiesthealth.digitaltwin.repository.EhrRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EHRService {
    private final EhrRecordRepository ehrRecordRepository;
    private final AuditService auditService;

    public List<EhrRecord> getRecordsForPatient(Long patientId) {
        return ehrRecordRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
    }

    @Transactional
    public Map<String, Object> importEhrCsv(Long patientId, MultipartFile file) throws Exception {
        Map<String, Object> report = new HashMap<>();
        List<String> errors = new ArrayList<>();
        List<EhrRecord> validRecords = new ArrayList<>();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded EHR file is empty.");
        }

        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are currently supported for EHR import.");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("EHR file has no header.");
            }

            String[] headers = headerLine.split(",");
            Map<String, Integer> colMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                colMap.put(headers[i].trim().toLowerCase(), i);
            }

            // Validate required columns
            if (!colMap.containsKey("record_type") || !colMap.containsKey("description")) {
                throw new IllegalArgumentException("Required columns 'record_type' and 'description' are missing.");
            }

            String line;
            int rowNum = 1;
            while ((line = reader.readLine()) != null) {
                rowNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = line.split(",", -1);
                try {
                    String recordType = tokens[colMap.get("record_type")].trim().toUpperCase();
                    String description = tokens[colMap.get("description")].trim();
                    String code = colMap.containsKey("code") ? tokens[colMap.get("code")].trim() : null;
                    String unit = colMap.containsKey("unit") ? tokens[colMap.get("unit")].trim() : null;
                    String textValue = colMap.containsKey("text_value") ? tokens[colMap.get("text_value")].trim() : null;
                    
                    Double numericValue = null;
                    if (colMap.containsKey("numeric_value")) {
                        String numStr = tokens[colMap.get("numeric_value")].trim();
                        if (!numStr.isEmpty()) {
                            numericValue = Double.parseDouble(numStr);
                        }
                    }

                    LocalDateTime recordedAt = LocalDateTime.now();
                    if (colMap.containsKey("recorded_at")) {
                        String dateStr = tokens[colMap.get("recorded_at")].trim();
                        if (!dateStr.isEmpty()) {
                            try {
                                recordedAt = LocalDateTime.parse(dateStr.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME);
                            } catch (Exception e) {
                                recordedAt = LocalDateTime.now();
                            }
                        }
                    }

                    // Range sanity checks
                    if (numericValue != null && numericValue < 0) {
                        errors.add("Row " + rowNum + ": Negative numeric value " + numericValue + " flagged as suspicious.");
                        continue;
                    }

                    EhrRecord record = EhrRecord.builder()
                            .patientId(patientId)
                            .recordType(recordType)
                            .code(code)
                            .description(description)
                            .numericValue(numericValue)
                            .unit(unit)
                            .textValue(textValue)
                            .sourceFile(filename)
                            .recordedAt(recordedAt)
                            .build();

                    validRecords.add(record);
                } catch (Exception e) {
                    errors.add("Row " + rowNum + " parse error: " + e.getMessage());
                }
            }
        }

        if (!validRecords.isEmpty()) {
            ehrRecordRepository.saveAll(validRecords);
            auditService.logAction(1L, patientId, "IMPORT", "EHR_RECORDS",
                    "Imported " + validRecords.size() + " records from " + filename);
        }

        report.put("totalParsed", validRecords.size() + errors.size());
        report.put("successfulImports", validRecords.size());
        report.put("flaggedErrors", errors);
        report.put("filename", filename);
        report.put("importedAt", LocalDateTime.now());

        return report;
    }
}

