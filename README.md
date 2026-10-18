# Happiest Healthy Digital Twin Platform

> **A Living Virtual Physiological Model for Chronic Disease Tracking, Evidence-Backed Insights, and Counterfactual What-If Simulations.**

Built for the **Happiest Healthy Digital Twin Challenge**.

---

## 1. Executive Summary

Traditional digital healthcare dashboards act merely as **passive telemetry viewers**: displaying isolated measurements without temporal context, biological coherence, or clinical trajectory. 

This project implements a true **Living Digital Twin** of a patient (Rahul Verma, 52M, hypertensive patient with a smartwatch) that:
1. **Unifies heterogeneous modalities**: Ingests authorized Electronic Health Records (EHR, diagnoses, LOINC lab panels, prescriptions) and high-frequency wearable sensor streams (Heart Rate, Steps, SpO₂, Sleep).
2. **Maintains architectural separation of the Four Data Planes**:
   - **Observed Data**: Raw, immutable measurements with source provenance and quality tags (`VALID`, `SUSPICIOUS`, `STALE`).
   - **Calculated Twin State**: Multi-metric temporal snapshot comparing continuous telemetry against personal baselines.
   - **Evidence-Backed Insights**: Explainable statistical shifts and clinical trends tied to specific data points.
   - **Predictions & Simulations**: Probabilistic future risk projections (ML) and hypothetical counterfactual interventions (*What-If* scenarios), strictly isolated from real clinical records.
3. **Accounts for real-world messiness**: Recognizes stale sensors, missing values, misaligned timestamps, and variable data quality rather than displaying misleading numbers.

---

## 2. System Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        TARGET ARCHITECTURE                             │
│                                                                        │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │                    React 18 / Vite Frontend                    │   │
│   │   Tailwind CSS + Recharts + Lucide Icons + Live Telemetry Feed │   │
│   └──────────────────────▲──────────────────▲──────────────────────┘   │
│                          │ REST             │ Real-Time Polling        │
│                          ▼                  │                          │
│   ┌────────────────────────────────────────────────────────────────┐   │
│   │                 Java Spring Boot 3.x Backend                   │   │
│   │         Auth, Validation, Coordination, Twin Engine,           │   │
│   │            Audit Logging, Interoperability (FHIR)              │   │
│   └──────────────────────┬──────────────────▲──────────────────────┘   │
│                          │                  │                          │
│             Spring Data  │                  │ Internal REST            │
│             JPA / JDBC   │                  │ (Pydantic DTOs)          │
│                          ▼                  ▼                          │
│   ┌──────────────────────────────┐  ┌──────────────────────────────┐   │
│   │            MySQL             │  │       Python + FastAPI       │   │
│   │  Patients, Readings, Twins,  │  │   Preprocessing, Statistics, │   │
│   │     Insights, Predictions    │  │  Scikit-Learn/XGBoost Models │   │
│   └──────────────────────────────┘  └──────────────────────────────┘   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. The Core Modules

| Module | Technology | Responsibility |
| :--- | :--- | :--- |
| **Frontend** | React 18, Vite, Tailwind CSS, Recharts, Lucide Icons | Responsive clinical dashboard, living twin status visualizer, multi-series time-series charts, evidence inspector drawer, What-If scenario sliders, and EHR CSV importer. |
| **Backend Orchestrator** | Java 17 LTS, Spring Boot 3.2.x, Spring Data JPA | Access control, biological boundary validation, patient management, EHR CSV parser, wearable telemetry ingestion, twin state snapshot recalculation, audit ledger, and AI coordination. |
| **AI & Predictive Engine** | Python 3.11, FastAPI, Pydantic v2, Scikit-learn, NumPy | Trend analysis (7-day rolling sleep drop, resting HR elevation), calibrated 30-day cardiovascular exacerbation risk scoring with 95% CI and feature attribution, and counterfactual simulation engine. |
| **Database** | MySQL (with dual-mode persistent H2 fallback) | 11 core tables preserving immutable raw observations distinct from derived summaries, predictions, and simulation runs. |

---

## 4. End-to-End Judges' Demonstration Script

Follow this step-by-step workflow during judging:

### Step 1: Inspect the Patient Dashboard & Living Twin Snapshot
- Open `http://localhost:3000`.
- Observe **Rahul Verma**'s patient header: Age 52, Male, MRN-90214, Primary Condition: Essential Hypertension (ICD-10 I10).
- Observe the **Living Digital Twin Snapshot**: Resting HR (77 BPM, $+5$ BPM above baseline), Blood Pressure (142/88 mmHg, Stage 2), Sleep (6.2h, $-0.9$h deficit), Daily Steps (5,200), and SpO₂ (98%).

### Step 2: Stream Live Wearable Readings via the Simulator
- In the top toolbar, select the scenario: **"Hypertension Surge"** or **"Sleep Deficit Episode"**.
- Click **"Start Stream"** (or click the fast-forward **"Tick"** icon).
- Observe new timestamped readings arriving and updating the **Longitudinal Telemetry Chart** in real-time.

### Step 3: Ingest Authorized Hospital EHR Records
- Click **"EHR Records"** in the header.
- View Rahul's preloaded diagnoses (I10, E78.0), prescriptions (Amlodipine 5mg, Atorvastatin 10mg), and LOINC laboratory panels (Serum Creatinine 1.05 mg/dL, Fasting Glucose 104 mg/dL, HbA1c 5.8%, Total Cholesterol 215 mg/dL).
- Click **"Choose File"** and upload `data/sample_ehr_rahul.csv`.
- Notice the validation report: records parsed, mapped, validated, and saved to MySQL.

### Step 4: Review Traceable AI Insights
- Scroll to the **Evidence-Backed Clinical Insights** feed.
- Notice the finding: *"Sleep Duration Decreased by -12.7%"* and *"Resting Heart Rate Elevated (+5.0 BPM Above Baseline)"*.
- Click the chevron to expand the **Traceable Scientific Evidence** drawer.
- Point out the exact recent record IDs, 7-day rolling window dates, and confidence score ($92\%$).

### Step 5: Evaluate the Predictive Risk Model
- View the **Predictive Health Outcome** gauge: **30-Day Cardiovascular Exacerbation Risk** ($14.8\%$, Moderate Risk).
- Point out the calibrated **$95\%$ Confidence Interval**: $[11.3\% – 18.3\%]$ and model version (`cardio-twin-rf-v1.2`).
- Inspect the **Influential Input Factors (Feature Attribution)** horizontal bars showing Systolic BP and Sleep Deficit as risk drivers, with Medication Adherence as a protective factor.

### Step 6: Execute the Standout Feature — What-If Simulation
- Scroll to the **What-If Counterfactual Intervention Sandbox**.
- Adjust the hypothetical sliders:
  - Increase **Daily Sleep** from $6.2\text{h} \rightarrow 8.0\text{h}$.
  - Increase **Daily Steps** from $5,200 \rightarrow 9,000\text{ steps}$.
  - Elevate **Medication Adherence** to $100\%$.
  - Target **Systolic BP** to $124\text{ mmHg}$.
- Click **"Execute Simulation Scenario"**.
- Point out the **Side-by-Side Comparison**:
  - Baseline Risk: **$14.8\%$**
  - Simulated Risk: **$7.2\%$**
  - Delta: **$-51.4\%$ Relative Risk Reduction**.
- Emphasize to the judges: **Zero Mutation of Real Records** — Rahul's actual EHR and wearable data remain completely untouched in MySQL!

### Step 7: Prove End-to-End Traceability
- Click **"Traceability"** in the top header.
- Inspect the tamper-evident audit ledger listing every single creation, import, simulation run, and quality check with timestamps and resource IDs.

---

## 5. How to Run the Entire Project

### Prerequisites
- **Java 17 LTS** (installed)
- **Node.js LTS & npm** (installed in `D:\DevTools\nodejs`)
- **Python 3.11** (installed)

### 1. Start the Python AI Microservice (Port 8000)
```powershell
cd ai_service
python -m uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```
*Health Check*: `http://localhost:8000/health`  
*Interactive Swagger API Docs*: `http://localhost:8000/docs`

### 2. Start the Java Spring Boot Backend (Port 8080)
```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
& "D:\DevTools\maven\bin\mvn.cmd" spring-boot:run
```
*Backend API*: `http://localhost:8080/api/patients/1/twin`

### 3. Start the React Frontend (Port 3000)
```powershell
cd frontend
& "D:\DevTools\nodejs\npm.cmd" run dev
```
*Open in Browser*: `http://localhost:3000`

---

## 6. Database Schema Summary

The canonical schema is defined in `data/schema.sql` and includes:
- `users`: Clinicians and roles.
- `patients`: Demographic and clinical master record.
- `consents`: Granular consent tracking.
- `data_sources`: Device and integration provenance.
- `ehr_records`: Structured medical history, LOINC lab panels, prescriptions.
- `wearable_data`: High-frequency timestamped sensor measurements.
- `twin_states`: Calculated longitudinal snapshots.
- `health_insights`: Statistical observations with evidence citations.
- `predictions`: Model versions, horizon, estimated probability, and CI.
- `simulation_runs`: Hypothetical scenarios and comparative deltas.
- `audit_logs`: Tamper-evident ledger.

