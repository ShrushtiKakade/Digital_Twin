import React, { useState } from 'react';
import { 
  Sliders, Play, RefreshCw, ArrowRight, ShieldAlert, Sparkles, 
  TrendingDown, TrendingUp, CheckCircle2, AlertCircle 
} from 'lucide-react';
import { api } from '../services/api';

export default function WhatIfSimulator({ patient, twinState, onSimulationSuccess }) {
  const [sleepHours, setSleepHours] = useState(7.5);
  const [dailySteps, setDailySteps] = useState(8500);
  const [medAdherence, setMedAdherence] = useState(0.95);
  const [systolicBp, setSystolicBp] = useState(128);

  const [loading, setLoading] = useState(false);
  const [simulationResult, setSimulationResult] = useState(null);

  const handleRunSimulation = async () => {
    setLoading(true);
    try {
      const inputs = {
        sleepHours,
        dailySteps,
        medicationAdherence: medAdherence,
        systolicBp
      };
      const res = await api.executeSimulation(
        patient?.id || 1, 
        'Clinician Counterfactual Exploration', 
        inputs
      );
      setSimulationResult(res.data);
      if (onSimulationSuccess) onSimulationSuccess(res.data);
    } catch (e) {
      alert('Error running What-If scenario: ' + e.message);
    } finally {
      setLoading(false);
    }
  };

  const baselineRisk = simulationResult 
    ? Math.round(simulationResult.baselineRiskScore * 1000) / 10 
    : 14.8;
  const simulatedRisk = simulationResult 
    ? Math.round(simulationResult.simulatedRiskScore * 1000) / 10 
    : 7.2;
  const delta = simulationResult 
    ? Math.round(simulationResult.deltaRiskScore * 1000) / 10 
    : -7.6;
  const relativePct = baselineRisk > 0 
    ? Math.round(((simulatedRisk - baselineRisk) / baselineRisk) * 1000) / 10 
    : -51.4;

  return (
    <div className="bg-slate-900 border border-purple-500/30 rounded-2xl p-6 shadow-2xl relative overflow-hidden">
      
      {/* Decorative Glow */}
      <div className="absolute top-0 right-0 w-80 h-80 bg-purple-500/5 rounded-full blur-3xl pointer-events-none" />

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between pb-4 border-b border-slate-800 gap-3">
        <div>
          <div className="flex items-center gap-2">
            <span className="p-1.5 rounded-lg bg-purple-950 text-purple-400 border border-purple-800">
              <Sliders className="w-4 h-4" />
            </span>
            <h3 className="text-sm font-bold text-white tracking-tight flex items-center gap-2">
              What-If Counterfactual Intervention Sandbox
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-purple-950 text-purple-400 border border-purple-800 uppercase">
                Zero-Mutation Sandbox
              </span>
            </h3>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Simulate hypothetical lifestyle & pharmacological changes without altering Rahul's real medical records
          </p>
        </div>

        <button
          onClick={handleRunSimulation}
          disabled={loading}
          className="px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white text-xs font-bold rounded-xl shadow-lg shadow-purple-500/20 flex items-center gap-2 transition cursor-pointer self-start sm:self-auto"
        >
          {loading ? <RefreshCw className="w-4 h-4 animate-spin" /> : <Play className="w-4 h-4" />}
          Execute Simulation Scenario
        </button>
      </div>

      {/* Grid: Interactive Controls & Side-by-Side Results */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 mt-6">
        
        {/* Sliders Column (5 cols) */}
        <div className="lg:col-span-5 space-y-4 bg-slate-950/60 p-4 rounded-xl border border-slate-800/80">
          <span className="text-xs font-semibold text-slate-300 block mb-3 uppercase tracking-wider">
            Hypothetical Parameter Sliders
          </span>

          {/* Sleep Hours Slider */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-slate-400">Hypothetical Daily Sleep:</span>
              <span className="font-bold text-indigo-400 font-mono">{sleepHours} Hours</span>
            </div>
            <input
              type="range"
              min="4"
              max="10"
              step="0.5"
              value={sleepHours}
              onChange={(e) => setSleepHours(parseFloat(e.target.value))}
              className="w-full accent-indigo-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
            />
            <div className="flex justify-between text-[10px] text-slate-600 mt-0.5">
              <span>4 hrs (Deficit)</span>
              <span>Current: {twinState?.sleepDailyAvg || 6.2}h</span>
              <span>10 hrs</span>
            </div>
          </div>

          {/* Steps Slider */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-slate-400">Daily Step Activity:</span>
              <span className="font-bold text-emerald-400 font-mono">{Number(dailySteps).toLocaleString()} Steps</span>
            </div>
            <input
              type="range"
              min="2000"
              max="15000"
              step="500"
              value={dailySteps}
              onChange={(e) => setDailySteps(parseInt(e.target.value))}
              className="w-full accent-emerald-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
            />
            <div className="flex justify-between text-[10px] text-slate-600 mt-0.5">
              <span>2k (Sedentary)</span>
              <span>Current: {twinState?.stepsDailyAvg || 5200}</span>
              <span>15k (Active)</span>
            </div>
          </div>

          {/* Medication Adherence */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-slate-400">Antihypertensive Adherence:</span>
              <span className="font-bold text-cyan-400 font-mono">{Math.round(medAdherence * 100)}%</span>
            </div>
            <input
              type="range"
              min="0.2"
              max="1.0"
              step="0.05"
              value={medAdherence}
              onChange={(e) => setMedAdherence(parseFloat(e.target.value))}
              className="w-full accent-cyan-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
            />
            <div className="flex justify-between text-[10px] text-slate-600 mt-0.5">
              <span>20% (Sporadic)</span>
              <span>Current: 75%</span>
              <span>100% (Strict)</span>
            </div>
          </div>

          {/* Systolic BP Target */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-slate-400">Target Systolic BP:</span>
              <span className="font-bold text-amber-400 font-mono">{systolicBp} mmHg</span>
            </div>
            <input
              type="range"
              min="110"
              max="165"
              step="1"
              value={systolicBp}
              onChange={(e) => setSystolicBp(parseInt(e.target.value))}
              className="w-full accent-amber-500 cursor-pointer h-1.5 bg-slate-800 rounded-lg"
            />
            <div className="flex justify-between text-[10px] text-slate-600 mt-0.5">
              <span>110 mmHg</span>
              <span>Current: {twinState?.bpSystolicAvg || 142} mmHg</span>
              <span>165 mmHg</span>
            </div>
          </div>
        </div>

        {/* Side-by-Side Comparison (7 cols) */}
        <div className="lg:col-span-7 flex flex-col justify-between">
          <div>
            <span className="text-xs font-semibold text-slate-300 block mb-3 uppercase tracking-wider">
              Comparative Risk Outcome (Side-by-Side)
            </span>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              
              {/* Baseline Card */}
              <div className="bg-slate-950 border border-slate-800 rounded-xl p-4">
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block mb-1">
                  1. Current Patient Baseline
                </span>
                <div className="text-3xl font-black text-slate-100 font-mono">
                  {baselineRisk}%
                </div>
                <div className="text-xs text-amber-400 font-medium mt-1">
                  Stage 2 Hypertensive Baseline
                </div>
                <div className="text-[11px] text-slate-500 mt-3 pt-2 border-t border-slate-800 space-y-0.5">
                  <div>Sleep: {twinState?.sleepDailyAvg || 6.2}h • Steps: {twinState?.stepsDailyAvg || 5200}</div>
                  <div>BP: {twinState?.bpSystolicAvg || 142}/{twinState?.bpDiastolicAvg || 88} mmHg</div>
                </div>
              </div>

              {/* Simulated Scenario Card */}
              <div className="bg-gradient-to-br from-purple-950/40 to-indigo-950/40 border border-purple-500/40 rounded-xl p-4 shadow-lg shadow-purple-500/10">
                <span className="text-[10px] font-bold text-purple-300 uppercase tracking-wider block mb-1 flex items-center gap-1">
                  <Sparkles className="w-3 h-3 text-purple-400" />
                  2. Simulated Intervention
                </span>
                <div className="text-3xl font-black text-purple-300 font-mono">
                  {simulatedRisk}%
                </div>
                <div className="text-xs text-emerald-400 font-semibold mt-1 flex items-center gap-1">
                  <TrendingDown className="w-3.5 h-3.5" />
                  {Math.abs(relativePct)}% Relative Risk Reduction
                </div>
                <div className="text-[11px] text-purple-200/70 mt-3 pt-2 border-t border-purple-900/50 space-y-0.5">
                  <div>Sleep: {sleepHours}h • Steps: {Number(dailySteps).toLocaleString()}</div>
                  <div>Target BP: {systolicBp} mmHg • Adh: {Math.round(medAdherence * 100)}%</div>
                </div>
              </div>

            </div>

            {/* Narrative Outcome Summary */}
            <div className="bg-slate-950 p-3.5 rounded-xl border border-slate-800 mt-4 text-xs text-slate-300 leading-relaxed flex items-center gap-3">
              <div className="p-2 rounded-lg bg-emerald-950 text-emerald-400 border border-emerald-800 shrink-0">
                <CheckCircle2 className="w-4 h-4" />
              </div>
              <div>
                <strong>Simulation Insight:</strong> Elevating nightly sleep to <strong>{sleepHours}h</strong> and increasing physical activity to <strong>{Number(dailySteps).toLocaleString()} steps</strong> shifts Rahul Verma's 30-day cardiovascular decompensation risk from <strong>{baselineRisk}%</strong> down to <strong>{simulatedRisk}%</strong>.
              </div>
            </div>
          </div>

          {/* Scientific Disclaimer Alert */}
          <div className="bg-slate-950/80 p-3 rounded-lg border border-slate-800/80 text-[10px] text-slate-400 mt-4 leading-relaxed flex items-start gap-2">
            <ShieldAlert className="w-4 h-4 text-purple-400 shrink-0 mt-0.5" />
            <span>
              <strong>Scientific Limitation:</strong> This simulation evaluates the mathematical response of the calibrated model to hypothetical parameter shifts. It does <em>not</em> prove an empirical clinical treatment effect, which requires validated interventional trials.
            </span>
          </div>

        </div>

      </div>

    </div>
  );
}
