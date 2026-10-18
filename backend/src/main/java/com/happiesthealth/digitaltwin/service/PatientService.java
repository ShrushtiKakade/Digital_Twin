package com.happiesthealth.digitaltwin.service;

import com.happiesthealth.digitaltwin.model.Patient;
import com.happiesthealth.digitaltwin.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final AuditService auditService;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> getPatientByMrn(String mrn) {
        return patientRepository.findByMrn(mrn);
    }

    @Transactional
    public Patient registerPatient(Patient patient) {
        if (patient.getMrn() == null || patient.getMrn().trim().isEmpty()) {
            throw new IllegalArgumentException("Medical Record Number (MRN) is required.");
        }
        if (patientRepository.findByMrn(patient.getMrn()).isPresent()) {
            throw new IllegalArgumentException("Patient with MRN " + patient.getMrn() + " already exists.");
        }

        Patient saved = patientRepository.save(patient);
        auditService.logAction(1L, saved.getId(), "CREATE", "PATIENT", "Registered new patient: " + saved.getFullName());
        return saved;
    }
}
