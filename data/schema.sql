-- Happiest Healthy Digital Twin Challenge
-- Canonical MySQL Schema for Patient Health Digital Twin

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'CLINICIAN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mrn VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    primary_condition VARCHAR(150),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS consents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    consent_type VARCHAR(100) NOT NULL,
    granted BOOLEAN NOT NULL DEFAULT TRUE,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP NULL,
    CONSTRAINT fk_consent_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS data_sources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    source_type VARCHAR(50) NOT NULL, -- WEARABLE_SIMULATOR, APPLE_HEALTH, EHR_UPLOAD, LAB_INTEGRATION
    source_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    last_sync_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_source_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ehr_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    record_type VARCHAR(50) NOT NULL, -- LAB_RESULT, DIAGNOSIS, MEDICATION, VITAL_SIGN
    code VARCHAR(50),                -- LOINC / ICD-10 code
    description VARCHAR(255) NOT NULL,
    numeric_value DOUBLE NULL,
    unit VARCHAR(50) NULL,
    text_value TEXT NULL,
    source_file VARCHAR(255) NULL,
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ehr_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wearable_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    metric_type VARCHAR(50) NOT NULL, -- HEART_RATE, STEPS, SPO2, SLEEP_HOURS, BLOOD_PRESSURE_SYS, BLOOD_PRESSURE_DIA
    metric_value DOUBLE NOT NULL,
    unit VARCHAR(50) NOT NULL,
    source VARCHAR(100) NOT NULL DEFAULT 'SIMULATOR',
    data_quality VARCHAR(30) NOT NULL DEFAULT 'VALID', -- VALID, SUSPICIOUS, STALE, INTERPOLATED
    recorded_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wearable_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_patient_metric_time (patient_id, metric_type, recorded_at)
);

CREATE TABLE IF NOT EXISTS twin_states (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    resting_hr_avg DOUBLE NULL,
    steps_daily_avg DOUBLE NULL,
    sleep_daily_avg DOUBLE NULL,
    spo2_avg DOUBLE NULL,
    bp_systolic_avg DOUBLE NULL,
    bp_diastolic_avg DOUBLE NULL,
    data_quality_score VARCHAR(30) NOT NULL DEFAULT 'ACCEPTABLE', -- OPTIMAL, ACCEPTABLE, DEGRADED, STALE
    metrics_snapshot_json LONGTEXT,
    provenance_record_ids TEXT,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_twin_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_twin_patient_time (patient_id, calculated_at)
);

CREATE TABLE IF NOT EXISTS health_insights (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    twin_state_id BIGINT NULL,
    category VARCHAR(50) NOT NULL, -- CARDIOVASCULAR, SLEEP_DEBT, ACTIVITY, MULTI_MODAL
    severity VARCHAR(30) NOT NULL DEFAULT 'INFO', -- INFO, LOW, MEDIUM, HIGH, WARNING
    title VARCHAR(255) NOT NULL,
    explanation TEXT NOT NULL,
    evidence_json LONGTEXT,
    confidence_score DOUBLE NOT NULL DEFAULT 0.85,
    data_quality_status VARCHAR(50) NOT NULL DEFAULT 'VALID_RECORDS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_insight_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_insight_twin FOREIGN KEY (twin_state_id) REFERENCES twin_states(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS simulation_runs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    scenario_name VARCHAR(150) NOT NULL,
    hypothetical_inputs_json LONGTEXT NOT NULL,
    baseline_metrics_json LONGTEXT NOT NULL,
    simulated_metrics_json LONGTEXT NOT NULL,
    baseline_risk_score DOUBLE NOT NULL,
    simulated_risk_score DOUBLE NOT NULL,
    delta_risk_score DOUBLE NOT NULL,
    scientific_disclaimer TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sim_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS predictions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    twin_state_id BIGINT NULL,
    outcome_type VARCHAR(100) NOT NULL, -- 30_DAY_CARDIO_RISK, HYPERTENSION_CRISIS
    prediction_horizon_days INT NOT NULL DEFAULT 30,
    estimated_risk DOUBLE NOT NULL,
    confidence_lower DOUBLE NULL,
    confidence_upper DOUBLE NULL,
    model_version VARCHAR(50) NOT NULL,
    data_quality_status VARCHAR(50) NOT NULL DEFAULT 'ACCEPTABLE',
    feature_attributions_json LONGTEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pred_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_pred_twin FOREIGN KEY (twin_state_id) REFERENCES twin_states(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    patient_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

