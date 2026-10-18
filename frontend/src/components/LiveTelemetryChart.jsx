import React, { useState } from 'react';
import { 
  ResponsiveContainer, LineChart, Line, AreaChart, Area, XAxis, YAxis, 
  Tooltip, CartesianGrid, ReferenceLine 
} from 'recharts';
import { Activity, Heart, Moon, Footprints } from 'lucide-react';

export default function LiveTelemetryChart({ wearableReadings }) {
  const [activeMetric, setActiveMetric] = useState('HEART_RATE');

  // Filter and format telemetry data
  const filtered = (wearableReadings || [])
    .filter(r => r.metricType === activeMetric)
    .map(r => ({
      id: r.id,
      value: r.value,
      timestamp: new Date(r.recordedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', month: 'short', day: 'numeric' }),
      rawTime: r.recordedAt,
      unit: r.unit,
      source: r.source,
      quality: r.dataQuality
    }))
    .reverse(); // chronological order

  const getMetricConfig = () => {
    switch (activeMetric) {
      case 'HEART_RATE':
        return {
          title: 'Heart Rate Stream',
          unit: 'BPM',
          color: '#f43f5e',
          refLine: 80,
          refLabel: 'Tachycardia Threshold (80 BPM)',
          yDomain: [50, 120]
        };
      case 'BLOOD_PRESSURE_SYS':
        return {
          title: 'Systolic Blood Pressure',
          unit: 'mmHg',
          color: '#f59e0b',
          refLine: 140,
          refLabel: 'Hypertension Stage 2 (140 mmHg)',
          yDomain: [100, 180]
        };
      case 'SLEEP_HOURS':
        return {
          title: 'Sleep Duration History',
          unit: 'Hours',
          color: '#818cf8',
          refLine: 7.0,
          refLabel: 'Clinical Recommended Minimum (7h)',
          yDomain: [4, 10]
        };
      case 'STEPS':
        return {
          title: 'Physical Activity',
          unit: 'Steps',
          color: '#10b981',
          refLine: 7000,
          refLabel: 'Daily Target (7,000 steps)',
          yDomain: [0, 12000]
        };
      default:
        return { title: 'Telemetry', unit: '', color: '#0ea5e9', refLine: null, yDomain: ['auto', 'auto'] };
    }
  };

  const config = getMetricConfig();

  return (
    <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6">
      
      {/* Chart Header & Metric Switcher Tabs */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between pb-4 border-b border-slate-800 gap-3">
        <div>
          <h3 className="text-sm font-bold text-white tracking-tight flex items-center gap-2">
            <Activity className="w-4 h-4 text-cyan-400" />
            Longitudinal Telemetry Timeline
          </h3>
          <p className="text-xs text-slate-400 mt-0.5">
            Synchronized sensor readings with personal baseline reference thresholds
          </p>
        </div>

        {/* Tab Buttons */}
        <div className="flex items-center bg-slate-950 p-1 rounded-xl border border-slate-800 gap-1">
          <button
            onClick={() => setActiveMetric('HEART_RATE')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              activeMetric === 'HEART_RATE' ? 'bg-rose-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Heart className="w-3.5 h-3.5" />
            Heart Rate
          </button>
          <button
            onClick={() => setActiveMetric('BLOOD_PRESSURE_SYS')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              activeMetric === 'BLOOD_PRESSURE_SYS' ? 'bg-amber-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Activity className="w-3.5 h-3.5" />
            BP Systolic
          </button>
          <button
            onClick={() => setActiveMetric('SLEEP_HOURS')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              activeMetric === 'SLEEP_HOURS' ? 'bg-indigo-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Moon className="w-3.5 h-3.5" />
            Sleep
          </button>
          <button
            onClick={() => setActiveMetric('STEPS')}
            className={`px-3 py-1.5 rounded-lg text-xs font-semibold flex items-center gap-1.5 transition ${
              activeMetric === 'STEPS' ? 'bg-emerald-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Footprints className="w-3.5 h-3.5" />
            Steps
          </button>
        </div>
      </div>

      {/* Recharts Canvas */}
      <div className="h-64 w-full mt-4">
        {filtered.length === 0 ? (
          <div className="h-full flex items-center justify-center text-xs text-slate-500">
            No telemetry records found for {config.title}. Start simulator to stream live data.
          </div>
        ) : (
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={filtered} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <defs>
                <linearGradient id="metricGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={config.color} stopOpacity={0.35}/>
                  <stop offset="95%" stopColor={config.color} stopOpacity={0.0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
              <XAxis 
                dataKey="timestamp" 
                stroke="#64748b" 
                fontSize={11}
                tickLine={false}
              />
              <YAxis 
                domain={config.yDomain} 
                stroke="#64748b" 
                fontSize={11}
                tickLine={false}
              />
              <Tooltip 
                content={({ active, payload }) => {
                  if (active && payload && payload.length) {
                    const data = payload[0].payload;
                    return (
                      <div className="bg-slate-950 border border-slate-800 rounded-lg p-2.5 shadow-xl text-xs">
                        <div className="text-slate-400 font-mono">{data.timestamp}</div>
                        <div className="text-base font-bold text-white mt-0.5">
                          {data.value} <span className="text-xs font-normal text-slate-400">{data.unit}</span>
                        </div>
                        <div className="text-[10px] text-slate-500 mt-1 flex items-center gap-2 border-t border-slate-800/80 pt-1">
                          <span>Source: {data.source}</span>
                          <span className="text-cyan-400 font-mono">ID: #{data.id}</span>
                        </div>
                      </div>
                    );
                  }
                  return null;
                }}
              />
              {config.refLine && (
                <ReferenceLine 
                  y={config.refLine} 
                  stroke="#ef4444" 
                  strokeDasharray="4 4" 
                  label={{ value: config.refLabel, fill: '#f87171', fontSize: 10, position: 'insideTopRight' }}
                />
              )}
              <Area 
                type="monotone" 
                dataKey="value" 
                stroke={config.color} 
                strokeWidth={2.5}
                fillOpacity={1} 
                fill="url(#metricGradient)" 
              />
            </AreaChart>
          </ResponsiveContainer>
        )}
      </div>

    </div>
  );
}

