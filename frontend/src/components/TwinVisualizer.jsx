import React from 'react';
import { 
  Heart, Moon, Footprints, Droplets, Activity, Clock, Database, 
  TrendingUp, TrendingDown, AlertCircle, CheckCircle2 
} from 'lucide-react';

export default function TwinVisualizer({ twinState, onInspectProvenance }) {
  const restingHr = twinState?.restingHrAvg || 74.0;
  const sleepHours = twinState?.sleepDailyAvg || 6.2;
  const dailySteps = twinState?.stepsDailyAvg || 5200;
  const sysBp = twinState?.bpSystolicAvg || 142.0;
  const diaBp = twinState?.bpDiastolicAvg || 88.0;
  const spo2 = twinState?.spo2Avg || 98.0;
  const quality = twinState?.dataQualityScore || 'OPTIMAL';
  const calculatedAt = twinState?.calculatedAt 
    ? new Date(twinState.calculatedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })
    : 'Just now';

  // Personal Baselines for Rahul Verma
  const hrDelta = Math.round((restingHr - 72.0) * 10) / 10;
  const sleepDelta = Math.round((sleepHours - 7.1) * 10) / 10;
  const sysDelta = Math.round((sysBp - 134.0) * 10) / 10;

  // Determine twin cardiovascular stress color
  const isElevated = sysBp >= 140 || restingHr >= 80 || sleepHours < 6.0;

  return (
    <div className={`bg-slate-900 border ${isElevated ? 'border-amber-500/40 twin-glow-danger' : 'border-slate-800 twin-glow'} rounded-2xl p-6 transition-all duration-300`}>
      
      {/* Top Banner */}
      <div className="flex flex-wrap items-center justify-between pb-4 border-b border-slate-800 gap-2">
        <div className="flex items-center gap-2.5">
          <span className="relative flex h-3 w-3">
            <span className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${isElevated ? 'bg-amber-400' : 'bg-cyan-400'}`} />
            <span className={`relative inline-flex rounded-full h-3 w-3 ${isElevated ? 'bg-amber-500' : 'bg-cyan-500'}`} />
          </span>
          <div>
            <h2 className="text-base font-bold text-white tracking-tight flex items-center gap-2">
              Living Digital Twin Snapshot
              <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                quality === 'OPTIMAL' ? 'bg-emerald-950 text-emerald-400 border border-emerald-800' :
                quality === 'STALE' ? 'bg-amber-950 text-amber-400 border border-amber-800' :
                'bg-blue-950 text-blue-400 border border-blue-800'
              }`}>
                {quality} Data Quality
              </span>
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">
              Continuously aggregated from validated EHR records & streaming wearable measurements
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3 text-xs text-slate-400">
          <span className="flex items-center gap-1 font-mono bg-slate-950 px-2.5 py-1 rounded-md border border-slate-800">
            <Clock className="w-3.5 h-3.5 text-cyan-400" />
            Updated: {calculatedAt}
          </span>
          {twinState?.provenanceRecordIds && (
            <button
              onClick={() => onInspectProvenance(twinState.provenanceRecordIds)}
              className="flex items-center gap-1 text-cyan-400 hover:text-cyan-300 underline underline-offset-2 transition"
            >
              <Database className="w-3.5 h-3.5" />
              Source Provenance
            </button>
          )}
        </div>
      </div>

      {/* Main Twin Physiological Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4 mt-5">
        
        {/* Heart Rate */}
        <div className="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Resting HR</span>
            <div className="p-2 rounded-lg bg-rose-950/40 text-rose-400 border border-rose-900/50">
              <Heart className="w-4 h-4 animate-heart-pulse" />
            </div>
          </div>
          <div className="my-2">
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl font-black text-white font-mono">{restingHr}</span>
              <span className="text-xs text-slate-400">BPM</span>
            </div>
            <div className="flex items-center gap-1 text-[11px] mt-1 font-medium">
              {hrDelta > 0 ? (
                <span className="text-amber-400 flex items-center gap-0.5">
                  <TrendingUp className="w-3 h-3" /> +{hrDelta} vs baseline
                </span>
              ) : (
                <span className="text-emerald-400 flex items-center gap-0.5">
                  <TrendingDown className="w-3 h-3" /> {hrDelta} vs baseline
                </span>
              )}
            </div>
          </div>
          <div className="text-[10px] text-slate-500 border-t border-slate-800/80 pt-2 flex justify-between">
            <span>Baseline: 72 BPM</span>
            <span className="text-slate-400 font-mono">7-day avg</span>
          </div>
        </div>

        {/* Blood Pressure */}
        <div className="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Blood Pressure</span>
            <div className="p-2 rounded-lg bg-amber-950/40 text-amber-400 border border-amber-900/50">
              <Activity className="w-4 h-4" />
            </div>
          </div>
          <div className="my-2">
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl font-black text-white font-mono">{sysBp}/{diaBp}</span>
              <span className="text-xs text-slate-400">mmHg</span>
            </div>
            <div className="flex items-center gap-1 text-[11px] mt-1 font-medium">
              {sysBp >= 140 ? (
                <span className="text-rose-400 flex items-center gap-0.5">
                  <AlertCircle className="w-3 h-3" /> Stage 2 Hypertensive
                </span>
              ) : (
                <span className="text-amber-400 flex items-center gap-0.5">
                  Stage 1 Borderline
                </span>
              )}
            </div>
          </div>
          <div className="text-[10px] text-slate-500 border-t border-slate-800/80 pt-2 flex justify-between">
            <span>Baseline: 134/84</span>
            <span className="text-slate-400 font-mono">LOINC 8480-6</span>
          </div>
        </div>

        {/* Sleep Duration */}
        <div className="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Sleep Duration</span>
            <div className="p-2 rounded-lg bg-indigo-950/40 text-indigo-400 border border-indigo-900/50">
              <Moon className="w-4 h-4" />
            </div>
          </div>
          <div className="my-2">
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl font-black text-white font-mono">{sleepHours}</span>
              <span className="text-xs text-slate-400">hrs/day</span>
            </div>
            <div className="flex items-center gap-1 text-[11px] mt-1 font-medium">
              {sleepDelta < 0 ? (
                <span className="text-rose-400 flex items-center gap-0.5">
                  <TrendingDown className="w-3 h-3" /> {sleepDelta}h sleep deficit
                </span>
              ) : (
                <span className="text-emerald-400 flex items-center gap-0.5">
                  <TrendingUp className="w-3 h-3" /> +{sleepDelta}h optimal
                </span>
              )}
            </div>
          </div>
          <div className="text-[10px] text-slate-500 border-t border-slate-800/80 pt-2 flex justify-between">
            <span>Prior: 7.1 hrs</span>
            <span className="text-slate-400 font-mono">7-day avg</span>
          </div>
        </div>

        {/* Physical Steps */}
        <div className="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Daily Steps</span>
            <div className="p-2 rounded-lg bg-emerald-950/40 text-emerald-400 border border-emerald-900/50">
              <Footprints className="w-4 h-4" />
            </div>
          </div>
          <div className="my-2">
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl font-black text-white font-mono">{Number(dailySteps).toLocaleString()}</span>
              <span className="text-xs text-slate-400">steps</span>
            </div>
            <div className="flex items-center gap-1 text-[11px] mt-1 font-medium">
              {dailySteps < 6000 ? (
                <span className="text-amber-400 flex items-center gap-0.5">
                  Sedentary trend
                </span>
              ) : (
                <span className="text-emerald-400 flex items-center gap-0.5">
                  Active ambulatory
                </span>
              )}
            </div>
          </div>
          <div className="text-[10px] text-slate-500 border-t border-slate-800/80 pt-2 flex justify-between">
            <span>Goal: 8,000</span>
            <span className="text-slate-400 font-mono">Daily avg</span>
          </div>
        </div>

        {/* Oxygen Saturation */}
        <div className="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex flex-col justify-between">
          <div className="flex items-center justify-between text-slate-400">
            <span className="text-xs font-semibold uppercase tracking-wider">Oxygen SpO2</span>
            <div className="p-2 rounded-lg bg-cyan-950/40 text-cyan-400 border border-cyan-900/50">
              <Droplets className="w-4 h-4" />
            </div>
          </div>
          <div className="my-2">
            <div className="flex items-baseline gap-1.5">
              <span className="text-2xl font-black text-white font-mono">{spo2}</span>
              <span className="text-xs text-slate-400">%</span>
            </div>
            <div className="flex items-center gap-1 text-[11px] mt-1 font-medium text-emerald-400">
              <CheckCircle2 className="w-3 h-3" /> Normal oxygenation
            </div>
          </div>
          <div className="text-[10px] text-slate-500 border-t border-slate-800/80 pt-2 flex justify-between">
            <span>Target: &gt;95%</span>
            <span className="text-slate-400 font-mono">Optical PPG</span>
          </div>
        </div>

      </div>

    </div>
  );
}
