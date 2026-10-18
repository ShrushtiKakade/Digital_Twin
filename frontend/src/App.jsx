import React, { useState, useEffect } from 'react';
import Header from './components/Header';
import TwinVisualizer from './components/TwinVisualizer';
import LiveTelemetryChart from './components/LiveTelemetryChart';
import InsightsFeed from './components/InsightsFeed';
import PredictionGauge from './components/PredictionGauge';
import WhatIfSimulator from './components/WhatIfSimulator';
import EhrModal from './components/EhrModal';
import AuditModal from './components/AuditModal';
import { api } from './services/api';

export default function App() {
  const [patient, setPatient] = useState(null);
  const [twinState, setTwinState] = useState(null);
  const [wearableReadings, setWearableReadings] = useState([]);
  const [insights, setInsights] = useState([]);
  const [prediction, setPrediction] = useState(null);
  const [ehrRecords, setEhrRecords] = useState([]);
  const [auditLogs, setAuditLogs] = useState([]);

  const [loadingPred, setLoadingPred] = useState(false);
  const [ehrModalOpen, setEhrModalOpen] = useState(false);
  const [auditModalOpen, setAuditModalOpen] = useState(false);

  const patientId = 1;

  useEffect(() => {
    fetchInitialData();
    // Continuous dynamic polling every 6 seconds to capture live simulator streaming
    const interval = setInterval(refreshTelemetryAndTwin, 6000);
    return () => clearInterval(interval);
  }, []);

  const fetchInitialData = async () => {
    try {
      const pRes = await api.getPatient(patientId);
      setPatient(pRes.data);
    } catch (e) {
      console.warn('Patient fetch failed; fallback to Rahul Verma');
      setPatient({
        id: 1,
        fullName: 'Rahul Verma',
        mrn: 'MRN-90214',
        gender: 'MALE',
        primaryCondition: 'Essential Hypertension (ICD-10 I10)'
      });
    }

    refreshTelemetryAndTwin();
    fetchEhrAndAudit();
  };

  const refreshTelemetryAndTwin = async () => {
    try {
      const [twinRes, wearRes, insRes, predRes] = await Promise.allSettled([
        api.getCurrentTwin(patientId),
        api.getLatestReadings(patientId, 40),
        api.getInsights(patientId),
        api.getPredictions(patientId)
      ]);

      if (twinRes.status === 'fulfilled') setTwinState(twinRes.value.data);
      if (wearRes.status === 'fulfilled') setWearableReadings(wearRes.value.data);
      if (insRes.status === 'fulfilled') setInsights(insRes.value.data);
      if (predRes.status === 'fulfilled' && predRes.value.data.length > 0) {
        setPrediction(predRes.value.data[0]);
      }
    } catch (e) {
      console.error('Error refreshing telemetry:', e);
    }
  };

  const fetchEhrAndAudit = async () => {
    try {
      const [ehrRes, auditRes] = await Promise.allSettled([
        api.getPatientEhr(patientId),
        api.getAuditLogs(patientId)
      ]);
      if (ehrRes.status === 'fulfilled') setEhrRecords(ehrRes.value.data);
      if (auditRes.status === 'fulfilled') setAuditLogs(auditRes.value.data);
    } catch (e) {
      console.error('Error fetching EHR / Audit:', e);
    }
  };

  const handleTriggerPrediction = async () => {
    setLoadingPred(true);
    try {
      const res = await api.triggerPrediction(patientId);
      setPrediction(res.data);
    } catch (e) {
      console.error('Error triggering prediction:', e);
    } finally {
      setLoadingPred(false);
    }
  };

  const handleInspectProvenance = (recordIds) => {
    fetchEhrAndAudit();
    setAuditModalOpen(true);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      
      {/* 1. Header Toolbar */}
      <Header
        patient={patient}
        twinState={twinState}
        onRefresh={refreshTelemetryAndTwin}
        onOpenEhr={() => { fetchEhrAndAudit(); setEhrModalOpen(true); }}
        onOpenAudit={() => { fetchEhrAndAudit(); setAuditModalOpen(true); }}
      />

      {/* Main Container */}
      <main className="max-w-7xl mx-auto w-full px-4 lg:px-8 py-6 space-y-6 flex-1">
        
        {/* 2. Living Digital Twin Physiological Status Banner */}
        <TwinVisualizer
          twinState={twinState}
          onInspectProvenance={handleInspectProvenance}
        />

        {/* 3. Analytical Middle Grid: Telemetry Chart & Predictive Outcome */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <div className="lg:col-span-7">
            <LiveTelemetryChart wearableReadings={wearableReadings} />
          </div>

          <div className="lg:col-span-5">
            <PredictionGauge
              prediction={prediction}
              onTriggerPrediction={handleTriggerPrediction}
              loading={loadingPred}
            />
          </div>
        </div>

        {/* 4. Evidence-Backed Insights Row */}
        <div>
          <InsightsFeed insights={insights} />
        </div>

        {/* 5. What-If Counterfactual Intervention Sandbox (Standout Feature) */}
        <div>
          <WhatIfSimulator
            patient={patient}
            twinState={twinState}
            onSimulationSuccess={fetchEhrAndAudit}
          />
        </div>

      </main>

      {/* Modals */}
      <EhrModal
        isOpen={ehrModalOpen}
        onClose={() => setEhrModalOpen(false)}
        patientId={patientId}
        ehrRecords={ehrRecords}
        onImportSuccess={() => { refreshTelemetryAndTwin(); fetchEhrAndAudit(); }}
      />

      <AuditModal
        isOpen={auditModalOpen}
        onClose={() => setAuditModalOpen(false)}
        auditLogs={auditLogs}
      />

      {/* Footer */}
      <footer className="border-t border-slate-900 bg-slate-950/80 py-4 px-6 text-center text-xs text-slate-500">
        Happiest Healthy Digital Twin Challenge • Built with React, Java Spring Boot, Python FastAPI, and MySQL Architecture
      </footer>

    </div>
  );
}

