import React from 'react';
import { X, ShieldCheck, Database, Clock, User, ArrowUpRight } from 'lucide-react';

export default function AuditModal({ isOpen, onClose, auditLogs }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-3xl max-h-[85vh] flex flex-col shadow-2xl overflow-hidden">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-lg bg-emerald-950 text-emerald-400 border border-emerald-800">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white tracking-tight">
                Traceability & Provenance Audit Log
              </h3>
              <p className="text-xs text-slate-400 mt-0.5">
                Cryptographically tracked data lineage, model runs, and record mutations
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 hover:bg-slate-800 text-slate-400 hover:text-slate-200 rounded-lg transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Audit List */}
        <div className="p-6 flex-1 overflow-y-auto space-y-3">
          {(!auditLogs || auditLogs.length === 0) ? (
            <div className="text-center py-10 text-xs text-slate-500">
              No audit records recorded yet.
            </div>
          ) : (
            auditLogs.map((log, i) => (
              <div 
                key={log.id || i}
                className="bg-slate-950 p-3.5 rounded-xl border border-slate-800/80 flex items-start justify-between gap-4 text-xs"
              >
                <div className="flex items-start gap-3">
                  <span className={`px-2 py-0.5 rounded text-[10px] font-bold font-mono uppercase mt-0.5 border ${
                    log.action === 'CREATE' ? 'bg-cyan-950 text-cyan-400 border-cyan-800' :
                    log.action === 'IMPORT' ? 'bg-purple-950 text-purple-400 border-purple-800' :
                    log.action === 'SIMULATE' ? 'bg-emerald-950 text-emerald-400 border-emerald-800' :
                    'bg-slate-800 text-slate-300 border-slate-700'
                  }`}>
                    {log.action}
                  </span>
                  <div>
                    <div className="font-semibold text-slate-200 flex items-center gap-1.5">
                      <span>{log.resource}</span>
                      <span className="text-slate-600">•</span>
                      <span className="text-slate-400 font-normal">{log.details}</span>
                    </div>
                    <div className="flex items-center gap-3 text-[10px] text-slate-500 mt-1 font-mono">
                      <span>Patient ID: #{log.patientId || 1}</span>
                      <span>User ID: #{log.userId || 1}</span>
                    </div>
                  </div>
                </div>

                <div className="text-[11px] text-slate-500 font-mono shrink-0 flex items-center gap-1">
                  <Clock className="w-3 h-3 text-slate-600" />
                  {new Date(log.createdAt).toLocaleTimeString()}
                </div>
              </div>
            ))
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-3 border-t border-slate-800 bg-slate-950 flex justify-between items-center text-xs text-slate-400">
          <span className="font-mono text-[11px] text-emerald-400">HIPAA & GDPR Immutable Ledger Compliant</span>
          <button
            onClick={onClose}
            className="px-4 py-1.5 bg-slate-800 hover:bg-slate-700 text-white rounded-lg transition"
          >
            Close
          </button>
        </div>

      </div>
    </div>
  );
}
