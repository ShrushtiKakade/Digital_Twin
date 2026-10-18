"""
Generates professional PPTX presentation and architecture diagram deck
for Unstop Digital Twin Challenge 2026 Submission.
"""
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.enum.text import PP_ALIGN
from pptx.dml.color import RGBColor
import os

def create_presentation():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6]

    DARK_BG = RGBColor(15, 23, 42)      # Slate 900
    CARD_BG = RGBColor(30, 41, 59)      # Slate 800
    CYAN_ACCENT = RGBColor(14, 165, 233) # Cyan 500
    PURPLE_ACCENT = RGBColor(168, 85, 247) # Purple 500
    TEXT_LIGHT = RGBColor(241, 245, 249) # Slate 100
    TEXT_MUTED = RGBColor(148, 163, 184) # Slate 400
    EMERALD_ACCENT = RGBColor(16, 185, 129)

    slides_content = [
        {
            "title": "Happiest Healthy Digital Twin",
            "subtitle": "A Living Virtual Physiological Model for Chronic Disease Tracking, Evidence-Backed Insights, and Counterfactual What-If Simulations",
            "points": [
                "Digital Twin Challenge 2026 — Prototype & Code Submission",
                "Team Name: Team Digital Twin",
                "Team Members: Shrushti Kakade & Chaitanya",
                "College / Incubator: Engineering & Health Sciences Innovation Incubator",
                "GitHub: https://github.com/ShrushtiKakade/Digital_Twin.git"
            ]
        },
        {
            "title": "Problem Statement & Clinical Motivation",
            "subtitle": "Moving Beyond Passive Dashboards to an Active Computational Model",
            "points": [
                "The Telemetry Gap: Traditional apps display isolated vitals (HR, sleep, steps) with zero biological context or longitudinal coherence.",
                "The Clinical Need: Hypertensive patients like Rahul (52M) experience acute exacerbations between sparse outpatient clinic visits.",
                "Our Solution: A dynamic Digital Twin continuously aggregating longitudinal EHR records and high-frequency wearable sensor streams.",
                "Real-World Messiness Handled: Active identification of stale sensors, missing units, out-of-range anomalies, and temporal drift."
            ]
        },
        {
            "title": "Healthcare Use Cases & Target Users",
            "subtitle": "Bridging Outpatient Care, Personal Health Agency, and Clinical Research",
            "points": [
                "Primary Clinicians (Cardiologists): Inter-visit surveillance, automated clinical trend identification, and risk stratifications.",
                "Patients (Rahul Verma - 52M Hypertensive): Real-time physiological twin feedback, sleep debt alerts, and lifestyle empowerment.",
                "Clinical Researchers: In silico counterfactual experimentation to simulate outcome trajectories without patient risk.",
                "Health System Administrators: Interoperability via HL7 FHIR standards, role-based access control, and immutable audit trails."
            ]
        },
        {
            "title": "System Architecture & Technology Stack",
            "subtitle": "Modular, Polyglot, and Horizontally Scalable Multi-Tier Infrastructure",
            "points": [
                "Frontend: React 18, Vite, Tailwind CSS, Recharts for time-series charts, and Lucide icons.",
                "Backend Orchestrator: Java 17 LTS + Spring Boot 3.2 (REST Gateway, Validation, Twin Engine, Audit Ledger).",
                "AI & Predictive Microservice: Python 3.11 + FastAPI + Uvicorn (Scikit-Learn, NumPy, Pydantic v2).",
                "Relational Database: MySQL 8.x (11 relational tables with strict foreign keys, indexes, and provenance links).",
                "Security & Boundaries: Frontend never directly queries the database or invokes ML models directly; Spring Boot enforces RBAC."
            ]
        },
        {
            "title": "The Four Architectural Data Planes",
            "subtitle": "Strict Separation of Provenance, Calculations, Insights, and Sandbox Projections",
            "points": [
                "1. Observed Data (Raw & Immutable): Time-series sensor streams and laboratory observations with source provenance tags.",
                "2. Calculated Twin State: Continually recalculated 7-day and 24-hour moving baselines reflecting evolving physiological state.",
                "3. Evidence-Backed Insights: Statistical change detection linked directly to underlying record IDs with confidence scoring.",
                "4. Predictive & Simulation Plane: Calibrated ML risk projections and zero-mutation hypothetical 'What-If' scenarios."
            ]
        },
        {
            "title": "Continuous Ingestion & Digital Twin Updating",
            "subtitle": "High-Frequency Wearables Meets Longitudinal Electronic Health Records",
            "points": [
                "Wearable Ingestion & Simulator: Dual-mode engine supporting live streaming telemetry and controllable anomaly scenarios.",
                "EHR Ingestion Pipeline: Ingests LOINC laboratory panels (Serum Creatinine, HbA1c, Lipids) and ICD-10 diagnoses via validated CSV/JSON.",
                "Automated Recalculation: Ingestion triggers asynchronous baseline delta comparison against patient historical records.",
                "Living Snapshot Versioning: Every state calculation is permanently indexed with exact source record provenance references."
            ]
        },
        {
            "title": "AI/ML Framework & Predictive Modeling",
            "subtitle": "Calibrated 30-Day Cardiovascular Exacerbation Risk Forecasting",
            "points": [
                "Specific Prediction Target: 30-Day Cardiovascular Decompensation Probability for hypertensive cohort.",
                "Feature Engineering: Integrates Systolic BP, Resting HR, 7-Day Sleep Deficit, Ambulatory Steps, and Medication Adherence.",
                "Statistical Calibration: Logistic Regression / Random Forest calibrated to epidemiologic cardiovascular risk guidelines.",
                "Transparent Explainability: Outputs 95% Confidence Intervals and horizontal Feature Attribution bars (SHAP-style weights)."
            ]
        },
        {
            "title": "The Standout Feature: What-If Counterfactual Sandbox",
            "subtitle": "In Silico Scenario Modeling with Zero Mutation of Real Patient Records",
            "points": [
                "Interactive Parameter Sliders: Clinicians adjust hypothetical daily sleep, ambulatory steps, BP target, and adherence.",
                "Zero-Mutation Architecture: Sandbox runs in isolated memory; Rahul's genuine EHR and telemetry remain completely untouched.",
                "Side-by-Side Comparison: Direct visual comparison showing Baseline Risk (14.8%) vs Simulated Risk (7.2%) with -51.4% relative delta.",
                "Scientific Integrity: Transparent clinical disclaimer clarifying mathematical sensitivity versus causal treatment claims."
            ]
        },
        {
            "title": "Traceability, Security & Interoperability",
            "subtitle": "Healthcare-Grade Governance and Ethical AI Design",
            "points": [
                "Tamper-Evident Audit Ledger: Full logging of every registration, CSV ingestion, simulation run, and threshold breach.",
                "Data Provenance: Clicking any metric or insight displays the exact source records, timestamps, and confidence scores.",
                "Interoperability Standards: Data structures align with HL7 FHIR R5 Patient, Observation, and Condition resources.",
                "Clinical Non-Diagnostic Disclaimer: Built strictly as a clinical decision support prototype adhering to FDA/CDSCO software guidelines."
            ]
        },
        {
            "title": "Project Outcomes & Demonstration Roadmap",
            "subtitle": "Ready for End-to-End Evaluation",
            "points": [
                "Full-Stack Operational: Tested and verified across Python FastAPI, Spring Boot 3, and React 18.",
                "Synthetic Clinical Profile: Seeded with Rahul Verma (52M) including 14 days of historical telemetry and clinical vitals.",
                "Public GitHub Repository: https://github.com/ShrushtiKakade/Digital_Twin.git",
                "Open Source License: MIT License included in repository.",
                "Next Steps: Integration with authorized hospital FHIR APIs and prospective clinical validation."
            ]
        }
    ]

    for item in slides_content:
        slide = prs.slides.add_slide(blank_layout)

        # Background shape
        bg = slide.shapes.add_shape(1, 0, 0, prs.slide_width, prs.slide_height)
        bg.fill.solid()
        bg.fill.fore_color.rgb = DARK_BG
        bg.line.fill.background()

        # Header Box
        title_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.6), Inches(11.7), Inches(1.3))
        tf = title_box.text_frame
        tf.word_wrap = True
        
        p = tf.paragraphs[0]
        p.text = item["title"]
        p.font.size = Pt(28)
        p.font.bold = True
        p.font.color.rgb = CYAN_ACCENT

        p2 = tf.add_paragraph()
        p2.text = item["subtitle"]
        p2.font.size = Pt(14)
        p2.font.color.rgb = TEXT_MUTED

        # Content Card
        card = slide.shapes.add_shape(1, Inches(0.8), Inches(2.1), Inches(11.7), Inches(4.7))
        card.fill.solid()
        card.fill.fore_color.rgb = CARD_BG
        card.line.color.rgb = RGBColor(51, 65, 85)

        content_box = slide.shapes.add_textbox(Inches(1.1), Inches(2.3), Inches(11.1), Inches(4.3))
        ctf = content_box.text_frame
        ctf.word_wrap = True

        for i, pt in enumerate(item["points"]):
            cp = ctf.add_paragraph() if i > 0 else ctf.paragraphs[0]
            cp.text = "•  " + pt
            cp.font.size = Pt(15)
            cp.font.color.rgb = TEXT_LIGHT
            cp.space_after = Pt(14)

    output_path = os.path.join("docs", "Digital_Twin_Presentation.pptx")
    prs.save(output_path)
    print(f"Created presentation at: {output_path}")

def create_architecture_deck():
    prs = Presentation()
    prs.slide_width = Inches(13.333)
    prs.slide_height = Inches(7.5)
    blank_layout = prs.slide_layouts[6]

    DARK_BG = RGBColor(15, 23, 42)
    CARD_BG = RGBColor(30, 41, 59)
    CYAN_ACCENT = RGBColor(14, 165, 233)
    TEXT_LIGHT = RGBColor(241, 245, 249)
    TEXT_MUTED = RGBColor(148, 163, 184)

    slide = prs.slides.add_slide(blank_layout)
    bg = slide.shapes.add_shape(1, 0, 0, prs.slide_width, prs.slide_height)
    bg.fill.solid()
    bg.fill.fore_color.rgb = DARK_BG
    bg.line.fill.background()

    # Title
    title_box = slide.shapes.add_textbox(Inches(0.8), Inches(0.5), Inches(11.7), Inches(1.0))
    tf = title_box.text_frame
    p = tf.paragraphs[0]
    p.text = "Happiest Healthy Digital Twin — Complete System Architecture"
    p.font.size = Pt(26)
    p.font.bold = True
    p.font.color.rgb = CYAN_ACCENT

    # Tier 1: Client / Frontend
    card1 = slide.shapes.add_shape(1, Inches(0.8), Inches(1.8), Inches(3.6), Inches(4.8))
    card1.fill.solid()
    card1.fill.fore_color.rgb = CARD_BG
    card1.line.color.rgb = RGBColor(14, 165, 233)
    t1 = slide.shapes.add_textbox(Inches(0.9), Inches(1.9), Inches(3.4), Inches(4.6)).text_frame
    t1.word_wrap = True
    p1 = t1.paragraphs[0]
    p1.text = "PRESENTATION TIER (React 18)"
    p1.font.bold = True
    p1.font.size = Pt(14)
    p1.font.color.rgb = CYAN_ACCENT
    bullets1 = [
        "Patient Twin Visualizer (Vital Deltas)",
        "Recharts Multi-Series Telemetry Charts",
        "Evidence-Backed Insights Feed",
        "30-Day Risk Gauge (95% CI & SHAP)",
        "What-If Counterfactual Sandbox",
        "LOINC Lab Panels & CSV EHR Importer",
        "Traceability & Provenance Ledger"
    ]
    for b in bullets1:
        pb = t1.add_paragraph()
        pb.text = "• " + b
        pb.font.size = Pt(11)
        pb.font.color.rgb = TEXT_LIGHT

    # Tier 2: Orchestration / Spring Boot
    card2 = slide.shapes.add_shape(1, Inches(4.8), Inches(1.8), Inches(3.8), Inches(4.8))
    card2.fill.solid()
    card2.fill.fore_color.rgb = CARD_BG
    card2.line.color.rgb = RGBColor(16, 185, 129)
    t2 = slide.shapes.add_textbox(Inches(4.9), Inches(1.9), Inches(3.6), Inches(4.6)).text_frame
    t2.word_wrap = True
    p2 = t2.paragraphs[0]
    p2.text = "ORCHESTRATOR TIER (Java Spring Boot 3)"
    p2.font.bold = True
    p2.font.size = Pt(14)
    p2.font.color.rgb = RGBColor(16, 185, 129)
    bullets2 = [
        "Secure REST Gateway (CORS & RBAC)",
        "EHR CSV Parser & LOINC Field Mapper",
        "Biological Range & Unit Validation",
        "Wearable Telemetry Stream Handler",
        "Digital Twin Baseline State Engine",
        "AI Microservice Coordination (RestClient)",
        "Audit Trail & Provenance Registry",
        "Wearable Demo Scenario Simulator"
    ]
    for b in bullets2:
        pb = t2.add_paragraph()
        pb.text = "• " + b
        pb.font.size = Pt(11)
        pb.font.color.rgb = TEXT_LIGHT

    # Tier 3: AI Service & Database
    card3 = slide.shapes.add_shape(1, Inches(8.9), Inches(1.8), Inches(3.6), Inches(2.3))
    card3.fill.solid()
    card3.fill.fore_color.rgb = CARD_BG
    card3.line.color.rgb = RGBColor(168, 85, 247)
    t3 = slide.shapes.add_textbox(Inches(9.0), Inches(1.9), Inches(3.4), Inches(2.1)).text_frame
    t3.word_wrap = True
    p3 = t3.paragraphs[0]
    p3.text = "AI / ML TIER (Python FastAPI)"
    p3.font.bold = True
    p3.font.size = Pt(14)
    p3.font.color.rgb = RGBColor(168, 85, 247)
    bullets3 = [
        "Statistical Trend & Evidence Engine",
        "Calibrated 30-Day Cardio Risk ML",
        "Zero-Mutation What-If Sandbox",
        "Uncertainty & SHAP Feature Weights"
    ]
    for b in bullets3:
        pb = t3.add_paragraph()
        pb.text = "• " + b
        pb.font.size = Pt(11)
        pb.font.color.rgb = TEXT_LIGHT

    card4 = slide.shapes.add_shape(1, Inches(8.9), Inches(4.3), Inches(3.6), Inches(2.3))
    card4.fill.solid()
    card4.fill.fore_color.rgb = CARD_BG
    card4.line.color.rgb = RGBColor(245, 158, 11)
    t4 = slide.shapes.add_textbox(Inches(9.0), Inches(4.4), Inches(3.4), Inches(2.1)).text_frame
    t4.word_wrap = True
    p4 = t4.paragraphs[0]
    p4.text = "DATA TIER (MySQL 8 Relational)"
    p4.font.bold = True
    p4.font.size = Pt(14)
    p4.font.color.rgb = RGBColor(245, 158, 11)
    bullets4 = [
        "patients, ehr_records, wearable_data",
        "twin_states, health_insights",
        "predictions, simulation_runs",
        "audit_logs, data_sources, consents"
    ]
    for b in bullets4:
        pb = t4.add_paragraph()
        pb.text = "• " + b
        pb.font.size = Pt(11)
        pb.font.color.rgb = TEXT_LIGHT

    output_path = os.path.join("docs", "Digital_Twin_Architecture_Diagram.pptx")
    prs.save(output_path)
    print(f"Created architecture deck at: {output_path}")

if __name__ == "__main__":
    create_presentation()
    create_architecture_deck()

