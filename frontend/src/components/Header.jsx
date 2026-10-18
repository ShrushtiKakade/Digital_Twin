import React, { useState, useEffect } from 'react';
import { 
  Heart, Activity, Play, Square, FastForward, FileText, 
  ShieldCheck, RefreshCw, AlertTriangle, UserCheck, Stethoscope
} from 'lucide-react';
import { api } from '../services/api';

export default function Header({ 
  patient, 
  twinState, 
  onRefresh, 
  onOpenEhr, 
  onOpenAudit 
}) {
  const [isSimulating, setIsSimulating] = useState(false);
  const [scenario, setScenario] = useState('NORMAL');
  const [loadingAction, setLoadingAction] = useState(false);

  useEffect(() => {
    checkSimulatorStatus();
    const interval = setInterval(checkSimulatorStatus, 8000);
    return () => clearInterval(interval);
  }, []);

  const checkSimulatorStatus = async () => {
    try {
      const res = await api.getSimulatorStatus();
      setIsSimulating(res.data.running);
      if (res.data.scenario) setScenario(res.data.scenario);
    } catch (e) {
      console.warn('Simulator status unreachable');
    }
  };

  const toggleSimulator = async () => {
    setLoadingAction(true);
    try {
      if (isSimulating) {
        await api.stopSimulator();
        setIsSimulating(false);
      } else {
        await api.startSimulator(patient?.id || 1, scenario);
        setIsSimulating(true);
      }
    } catch (e) {
      alert('Error toggling wearable simulator: ' + e.message);
    } finally {
      setLoadingAction(false);
    }
  };

  const handleTick = async () => {
    setLoadingAction(true);
    try {
      await api.triggerSimulatorTick(patient?.id || 1, scenario);
      onRefresh();
    } catch (e) {
      console.error(e);
    } finally {
      setLoadingAction(false);
    }
  };

  const isStale = twinState?.dataQualityScore === 'STALE';

  return (
    <header className="bg-slate-900 border-b border-slate-800 sticky top-0 z-40 px-4 lg:px-8 py-3.5">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        
        {/* Brand & Patient Info */}
        <div className="flex items-center space-x-4">
          <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-cyan-600 to-blue-500 flex items-center justify-center shadow-lg shadow-cyan-500/20">
            <Heart className="w-6 h-6 text-white animate-heart-pulse" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-lg font-bold tracking-tight text-white flex items-center gap-2">
                Happiest Healthy Digital Twin
                <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-cyan-950 text-cyan-400 border border-cyan-800">
                  v1.0 Live Twin
                </span>
              </h1>
            </div>
            <div className="flex items-center gap-3 text-xs text-slate-400 mt-0.5">
              <span className="flex items-center gap-1 font-medium text-slate-200">
                <UserCheck className="w-3.5 h-3.5 text-cyan-400" />
                {patient?.fullName || 'Rahul Verma'} (52M)
              </span>
              <span className="text-slate-600">•</span>
              <span className="font-mono text-slate-300">MRN: {patient?.mrn || 'MRN-90214'}</span>
              <span className="text-slate-600">•</span>
              <span className="text-amber-400 font-medium">{patient?.primaryCondition || 'Hypertension (ICD-10 I10)'}</span>
            </div>
          </div>
        </div>

        {/* Live Simulator & Action Toolbar */}
        <div className="flex flex-wrap items-center gap-2.5">
          
          {/* Quality / Staleness Badge */}
          <div className={`px-2.5 py-1 rounded-lg text-xs font-medium border flex items-center gap-1.5 ${
            isStale 
              ? 'bg-amber-950/60 text-amber-300 border-amber-800' 
              : 'bg-emerald-950/60 text-emerald-300 border-emerald-800'
          }`}>
            <span className={`w-2 h-2 rounded-full ${isStale ? 'bg-amber-400' : 'bg-emerald-400 animate-ping'}`} />
            {isStale ? 'Sensors Stale' : 'Live Sync Active'}
          </div>

          {/* Simulator Controls */}
          <div className="flex items-center bg-slate-950 border border-slate-800 rounded-lg p-1 gap-1">
            <select
              value={scenario}
              onChange={(e) => setScenario(e.target.value)}
              className="bg-transparent text-xs text-slate-200 border-none focus:outline-none px-2 py-1 font-medium cursor-pointer"
            >
              <option value="NORMAL" className="bg-slate-900">Baseline Telemetry</option>
              <option value="HYPERTENSION_SPIKE" className="bg-slate-900">Hypertension Surge</option>
              <option value="SLEEP_DEBT" className="bg-slate-900">Sleep Deficit Episode</option>
            </select>

            <button
              onClick={toggleSimulator}
              disabled={loadingAction}
              className={`px-2.5 py-1 rounded text-xs font-semibold flex items-center gap-1.5 transition ${
                isSimulating
                  ? 'bg-red-600 hover:bg-red-500 text-white'
                  : 'bg-cyan-600 hover:bg-cyan-500 text-white'
              }`}
            >
              {isSimulating ? <Square className="w-3.5 h-3.5" /> : <Play className="w-3.5 h-3.5" />}
              {isSimulating ? 'Stop Stream' : 'Start Stream'}
            </button>

            <button
              onClick={handleTick}
              disabled={loadingAction}
              title="Generate single instant reading"
              className="p-1 hover:bg-slate-800 text-slate-300 hover:text-white rounded transition"
            >
              <FastForward className="w-3.5 h-3.5" />
            </button>
          </div>

          {/* Clinical Modals */}
          <button
            onClick={onOpenEhr}
            className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium rounded-lg border border-slate-700 flex items-center gap-1.5 transition"
          >
            <FileText className="w-3.5 h-3.5 text-cyan-400" />
            EHR Records
          </button>

          <button
            onClick={onOpenAudit}
            className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium rounded-lg border border-slate-700 flex items-center gap-1.5 transition"
          >
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            Traceability
          </button>

          <button
            onClick={onRefresh}
            title="Recalculate Digital Twin"
            className="p-2 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white rounded-lg border border-slate-700 transition"
          >
            <RefreshCw className="w-3.5 h-3.5" />
          </button>
        </div>

      </div>
    </header>
  );
}
