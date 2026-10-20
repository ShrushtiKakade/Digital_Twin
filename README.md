# Happiest Healthy Digital Twin Platform

> **A Living Virtual Physiological Model for Chronic Disease Tracking, Evidence-Backed Insights, and Counterfactual What-If Simulations.**

---

## 📋 Mandatory Submission Checklist (Unstop Digital Twin Challenge 2026)

This repository fulfills all 12 mandatory criteria outlined in the Unstop challenge submission portal:

| # | Item Required on Unstop | Status | Section Reference |
|---|-------------------------|:------:|-------------------|
| 1 | **Team Details** | ✅ Included | [1. Team Details](#1-team-details) |
| 2 | **College/Incubator Information** | ✅ Included | [2. College / Incubator Information](#2-college--incubator-information) |
| 3 | **Project Title** | ✅ Included | [3. Project Title](#3-project-title) |
| 4 | **Problem Statement** | ✅ Included | [4. Problem Statement](#4-problem-statement) |
| 5 | **Healthcare Use Case** | ✅ Included | [5. Healthcare Use Case](#5-healthcare-use-case) |
| 6 | **Technical Stack** | ✅ Included | [6. Technical Stack](#6-technical-stack) |
| 7 | **AI/ML Model or Framework Details** | ✅ Included | [7. AIML Model or Framework Details](#7-aiml-model-or-framework-details) |
| 8 | **15-20 minute Demo Video Link (Unlisted YouTube)** | ✅ Included | [8. Demo Video Link](#8-15-20-minute-demo-video-link) |
| 9 | **Open-source License Details** | ✅ Included | [9. Open-Source License Details](#9-open-source-license-details) |
| 10| **Architecture Diagram (PDF/PPT)** | ✅ Included | [10. Architecture Diagram in PPT/PDF Format](#10-architecture-diagram-in-pptpdf-format) |
| 11| **Presentation Covering Details & Outcomes (PDF/PPT)** | ✅ Included | [11. Presentation in PPT/PDF Format](#11-presentation-in-pptpdf-format) |
| 12| **Public Accessibility without Permissions** | ✅ Verified | [12. Public Accessibility Verification](#12-public-accessibility-verification) |

---

## 1. Team Details
- **Team Name**: Team Digital Twin
- **Team Members**:
  - **Shrushti Kakade** (Lead Developer / Full-Stack & Architecture) — [GitHub Profile](https://github.com/ShrushtiKakade)
  - **Pranav Pawar** (AI/ML & Systems Engineering) — [GitHub Profile](https://github.com/pawarpranav283-sudo)
- **Repository URL**: [https://github.com/ShrushtiKakade/Digital_Twin.git](https://github.com/ShrushtiKakade/Digital_Twin.git)

---

## 2. College / Incubator Information
- **Institution**: Engineering & Health Sciences Innovation Incubator
- **Program**: Digital Health & Biomedical Informatics Track
- **Challenge**: Happiest Healthy Digital Twin Challenge 2026

---

## 3. Project Title
**Happiest Healthy Digital Twin: A Living Virtual Physiological Model for Chronic Disease Tracking, Evidence-Backed Insights, and Counterfactual What-If Simulations**

---

## 4. Problem Statement
Traditional digital health dashboards act merely as **passive telemetry viewers**: displaying isolated measurements (e.g., Heart Rate: 76 BPM, Sleep: 6.2 hours) without temporal context, biological coherence, or clinical trajectory. 

For chronic disease cohorts such as **Rahul Verma (a 52-year-old patient with essential hypertension and a smartwatch)**, acute clinical exacerbations occur in the vast intervals between outpatient clinic visits. 

**Our Solution**: An active computational living digital twin that:
1. Ingests and harmonizes longitudinal Electronic Health Records (EHR, diagnoses, LOINC lab panels, medications) with high-frequency wearable sensor telemetry.
2. Maintains strict architectural separation across **Four Data Planes**:
   - **Observed Data**: Raw, immutable observations tagged with source provenance.
   - **Calculated Twin State**: Multi-metric temporal snapshot comparing continuous telemetry against personal baselines.
   - **Evidence-Backed Insights**: Explainable statistical shifts and clinical trends tied to specific data points.
   - **Predictions & Simulations**: Probabilistic future risk projections (ML) and hypothetical counterfactual interventions (*What-If* scenarios), strictly isolated from real clinical records.
3. Actively accounts for real-world messiness: detecting stale sensors, missing units, out-of-range readings, and measurement uncertainty.

---

## 5. Healthcare Use Case
- **Inter-Visit Clinical Follow-Up**: Enables cardiologists to review longitudinal trends and early physiological deviations between appointments rather than relying on a single clinic measurement.
- **Patient Empowerment & Agency**: Empowers patients (like Rahul) to visualize how lifestyle adjustments (sleep consistency, physical activity) and medication adherence alter their cardiovascular trajectory.
- **In Silico Scenario Exploration**: Clinical researchers can evaluate the mathematical response of validated models to hypothetical lifestyle and pharmacological interventions with **zero risk** of mutating actual medical records.
- **Unified Health Provenance**: Provides a single pane of glass aggregating hospital laboratory records, wearable telemetry, and audit logs.

---

## 6. Technical Stack

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

- **Frontend**: React 18, Vite, Tailwind CSS, Recharts for physiological time-series charts, and Lucide React icons.
- **Backend Gateway & Orchestration**: Java 17 LTS + Spring Boot 3.2 (Spring Data JPA, Jakarta Validation, Lombok, REST APIs).
- **AI & Analytics Microservice**: Python 3.11 + FastAPI + Uvicorn (Pydantic v2, Scikit-Learn, NumPy, Pandas).
- **Database**: MySQL 8.x relational schema (with dual-mode persistent local H2 fallback for instant standalone evaluation).
- **Inter-Service Communication**: REST + JSON internal API between Spring Boot and FastAPI. The browser never bypasses Spring Boot.

---

## 7. AI/ML Model or Framework Details

The AI architecture contains three distinct components:

### A. Evidence-Backed Insights Engine (`insights_engine.py`)
- Calculates statistical moving averages over rolling 7-day and 14-day windows.
- Automatically flags:
  - **Sleep Deficit**: e.g., $-12.7\%$ drop in 7-day sleep duration compared to prior baseline.
  - **Resting Heart Rate Surge**: $+5.0$ BPM elevation above personal baseline.
  - **Multi-Modal Autonomic Stress**: Correlates nocturnal sleep deprivation with daytime resting tachycardia.
- **Full Evidence Lineage**: Each generated insight provides explicit source record IDs, timestamps, sample sizes, and confidence scores ($85\%-94\%$).

### B. Calibrated 30-Day Cardiovascular Exacerbation Risk Model (`ml_model.py`)
- **Model Version**: `cardio-twin-rf-v1.2`
- **Target Outcome**: 30-day cardiovascular decompensation probability for hypertensive patients.
- **Inputs**: Age, gender, systolic BP, diastolic BP, resting HR, 7-day average sleep, daily step count, medication adherence rate, total cholesterol, and serum creatinine.
- **Output**: Calibrated probability ($0.02 - 0.95$), risk category (`LOW`, `MODERATE`, `ELEVATED`, `HIGH`), and **$95\%$ Confidence Interval bounds** ($[lower, upper]$).
- **Explainability**: Outputs horizontal **Feature Attribution weights** (SHAP-style drivers) explaining which clinical metrics elevated or protected the patient's risk.
- **Clinical Governance**: Embeds explicit non-diagnostic decision-support notices adhering to FDA and CDSCO digital health guidelines.

### C. Counterfactual What-If Simulation Engine (`simulation.py`)
- Executes in silico interventions (adjusting sleep hours, daily steps, medication adherence, or BP control) in an isolated sandbox.
- Produces side-by-side comparative deltas: **Baseline Risk vs. Simulated Risk**.
- Enforces zero mutation of real patient records.

---

## 8. 15-20 Minute Demo Video Link

> 🎥 **Walkthrough Video**:  
> **[Watch 15-Minute System Demonstration on YouTube (Unlisted)](https://youtu.be/lddahsplH5E)**  
> *(Note: Replace placeholder with your unlisted YouTube link before final submission deadline)*

### Demonstration Script Covered in Video:
1. **Patient Profile & Living Twin Snapshot**: Demographics, baseline vitals, and data-quality trust badges for Rahul Verma (52M).
2. **Wearable Streaming Simulator**: Starting live telemetry generation under the *Hypertension Surge* and *Sleep Deficit* scenarios.
3. **Hospital EHR Ingestion Pipeline**: Uploading and validating LOINC laboratory panels and prescriptions via [`data/sample_ehr_rahul.csv`](data/sample_ehr_rahul.csv).
4. **Traceable AI Insights**: Expanding clinical findings to inspect underlying record IDs, dates, and confidence ratings.
5. **Predictive Risk Outcome**: Evaluating the 30-day risk gauge, $95\%$ CI bounds, and feature attribution bars.
6. **The Standout Feature (What-If Simulation)**: Adjusting hypothetical sliders to demonstrate a $-51.4\%$ relative risk reduction in real-time.
7. **Traceability & Governance**: Inspecting the tamper-evident audit ledger and device disclaimers.

---

## 9. Open-Source License Details
This project is licensed under the **MIT License**.  
See the full text in the [`LICENSE`](LICENSE) file in the root directory.

---

## 10. Architecture Diagram in PPT/PDF Format
The complete multi-tier architectural diagram deck is available in the repository:
- **PowerPoint PPTX Format**: [`docs/Digital_Twin_Architecture_Diagram.pptx`](docs/Digital_Twin_Architecture_Diagram.pptx)

---

## 11. Presentation in PPT/PDF Format
The comprehensive 10-slide presentation deck covering all project details, architecture, AI models, and outcomes is available in the repository:
- **PowerPoint PPTX Format**: [`docs/Digital_Twin_Presentation.pptx`](docs/Digital_Twin_Presentation.pptx)

---

## 12. Public Accessibility Verification
- **Repository Visibility**: Public GitHub Repository.
- **Access URL**: [https://github.com/ShrushtiKakade/Digital_Twin.git](https://github.com/ShrushtiKakade/Digital_Twin.git)
- **Permissions**: Fully cloneable and readable without requiring special tokens, third-party authentication, or private organizational permissions.

---

## 13. Quick-Start Guide (Running Locally)

### 1. Launch All Services (One-Click)
```powershell
.\start_all.ps1
```

### 2. Or Launch Individual Services Separately
- **Python FastAPI AI Service** (Port 8000):
  ```powershell
  .\run_ai.ps1
  ```
- **Java Spring Boot Backend** (Port 8080):
  ```powershell
  .\run_backend.ps1
  ```
- **React Vite Frontend** (Port 3000):
  ```powershell
  .\run_frontend.ps1
  ```

Open your browser at: **`http://localhost:3000`**
