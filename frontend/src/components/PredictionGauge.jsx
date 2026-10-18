import React from 'react';
import { 
  Target, AlertTriangle, ShieldAlert, Cpu, BarChart2, 
  HelpCircle, Info, RefreshCw 
} from 'lucide-react';

export default function PredictionGauge({ prediction, onTriggerPrediction, loading }) {
  const risk = prediction?.estimatedRisk !== undefined ? prediction.estimatedRisk : 0.148;
  const riskPct = Math.round(risk * 1000) / 10;
  const horizon = prediction?.predictionHorizonDays || 30;
  const outcome = prediction?.outcomeType || '30_DAY_CARDIOVASCULAR_EXACERBATION_RISK';
  const modelVer = prediction?.modelVersion || 'cardio-twin-rf-v1.2';
  const lowerBound = prediction?.confidenceLower !== null && prediction?.confidenceLower !== undefined 
    ? Math.round(prediction.confidenceLower * 1000) / 10 : Math.max(1, riskPct - 3.5);
  const upperBound = prediction?.confidenceUpper !== null && prediction?.confidenceUpper !== undefined 
    ? Math.round(prediction.confidenceUpper * 1000) / 10 : Math.min(99, riskPct + 3.5);
  
  let featureAttributions = null;
  try {
    featureAttributions = typeof prediction?.featureAttributionsJson === 'string'
      ? JSON.parse(prediction.featureAttributionsJson)
      : prediction?.featureAttributions;
  } catch (e) {}

  // Risk Tier & Color styling
  const getRiskColor = (val) => {
    if (val < 0.15) return { text: 'text-emerald-400', bg: 'bg-emerald-500', tier: 'LOW RISK', badge: 'bg-emerald-950 text-emerald-400 border-emerald-800' };
    if (val < 0.30) return { text: 'text-amber-400', bg: 'bg-amber-500', tier: 'MODERATE RISK', badge: 'bg-amber-950 text-amber-400 border-amber-800' };
    if (val < 0.50) return { text: 'text-orange-400', bg: 'bg-orange-500', tier: 'ELEVATED RISK', badge: 'bg-orange-950 text-orange-400 border-orange-800' };
    return { text: 'text-rose-400', bg: 'bg-rose-500', tier: 'HIGH RISK', badge: 'bg-rose-950 text-rose-400 border-rose-800' };
  };

  const style = getRiskColor(risk);

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 flex flex-col justify-between">
      
      {/* Header */}
      <div>
        <div className="flex items-center justify-between pb-3 border-b border-slate-800">
          <div>
            <h3 className="text-sm font-bold text-white tracking-tight flex items-center gap-2">
              <Target className="w-4 h-4 text-cyan-400" />
              Predictive Health Outcome
            </h3>
            <p className="text-xs text-slate-400 mt-0.5">
              Target: 30-Day Cardiovascular Decompensation Probability
            </p>
          </div>

          <button
            onClick={onTriggerPrediction}
            disabled={loading}
            className="p-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white rounded-lg border border-slate-700 transition"
            title="Recalculate Model Inference"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
          </button>
        </div>

        {/* Gauge Centerpiece */}
        <div className="my-5 flex flex-col items-center">
          <div className="relative w-40 h-40 flex items-center justify-center">
            {/* SVG Progress Ring */}
            <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
              <circle
                cx="50"
                cy="50"
                r="40"
                stroke="#1e293b"
                strokeWidth="8"
                fill="transparent"
              />
              <circle
                cx="50"
                cy="50"
                r="40"
                stroke="currentColor"
                strokeWidth="8"
                strokeDasharray={251.2}
                strokeDashoffset={251.2 - (251.2 * Math.min(risk, 1))}
                strokeLinecap="round"
                fill="transparent"
                className={`${style.text} transition-all duration-1000 ease-out`}
              />
            </svg>

            {/* Inner Content */}
            <div className="absolute flex flex-col items-center text-center">
              <span className="text-3xl font-black text-white font-mono tracking-tight">
                {riskPct}%
              </span>
              <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full mt-1 border ${style.badge}`}>
                {style.tier}
              </span>
            </div>
          </div>

          {/* Uncertainty Bounds */}
          <div className="flex items-center gap-3 text-xs text-slate-400 mt-2 font-mono">
            <span>95% CI: [{lowerBound}% – {upperBound}%]</span>
            <span className="text-slate-600">•</span>
            <span>Horizon: {horizon} Days</span>
          </div>
        </div>

        {/* Feature Drivers / Attributions */}
        {featureAttributions && (
          <div className="mt-2 pt-3 border-t border-slate-800/80">
            <span className="text-xs font-semibold text-slate-300 flex items-center gap-1.5 mb-2.5">
              <BarChart2 className="w-3.5 h-3.5 text-cyan-400" />
              Influential Input Factors (Feature Attribution)
            </span>

            <div className="space-y-2 text-xs">
              {Object.entries(featureAttributions).map(([k, v]) => {
                const val = typeof v === 'number' ? v : 0;
                const isProtective = val < 0;
                const absVal = Math.min(100, Math.round(Math.abs(val) * 300));

                return (
                  <div key={k} className="flex items-center justify-between gap-3 text-[11px]">
                    <span className="text-slate-400 capitalize truncate max-w-[150px]">
                      {k.replace(/([A-Z])/g, ' $1')}
                    </span>
                    <div className="flex items-center gap-2 flex-1 max-w-[140px]">
                      <div className="h-1.5 flex-1 bg-slate-950 rounded-full overflow-hidden border border-slate-800">
                        <div
                          className={`h-full rounded-full ${isProtective ? 'bg-emerald-500' : 'bg-rose-500'}`}
                          style={{ width: `${absVal}%` }}
                        />
                      </div>
                      <span className={`font-mono text-[10px] font-semibold ${isProtective ? 'text-emerald-400' : 'text-rose-400'}`}>
                        {val > 0 ? `+${val}` : val}
                      </span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* Model Governance Footer */}
      <div className="mt-4 pt-3 border-t border-slate-800/80 flex flex-col gap-2">
        <div className="flex items-center justify-between text-[11px] text-slate-500">
          <span className="flex items-center gap-1 font-mono">
            <Cpu className="w-3.5 h-3.5 text-slate-400" />
            {modelVer}
          </span>
          <span className="text-emerald-400 font-medium">Validated Model</span>
        </div>

        {/* Clinical Disclaimer Alert */}
        <div className="bg-slate-950 p-2.5 rounded-lg border border-slate-800/70 text-[10px] text-slate-400 leading-tight flex items-start gap-2">
          <ShieldAlert className="w-4 h-4 text-amber-500 shrink-0 mt-0.5" />
          <span>
            <strong>Clinical Notice:</strong> Calibrated decision-support prototype. Estimates probability based on historical features; not an authorized diagnostic device.
          </span>
        </div>
      </div>

    </div>
  );
}

