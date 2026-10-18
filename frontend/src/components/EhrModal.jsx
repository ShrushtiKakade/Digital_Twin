import React, { useState } from 'react';
import { 
  X, UploadCloud, FileText, CheckCircle2, AlertTriangle, 
  Pill, Activity, FlaskConical, Stethoscope 
} from 'lucide-react';
import { api } from '../services/api';

export default function EhrModal({ isOpen, onClose, patientId, ehrRecords, onImportSuccess }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [uploadReport, setUploadReport] = useState(null);
  const [filterType, setFilterType] = useState('ALL');

  if (!isOpen) return null;

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    setUploadReport(null);
    try {
      const res = await api.importEhrCsv(patientId || 1, file);
      setUploadReport(res.data);
      if (onImportSuccess) onImportSuccess();
    } catch (e) {
      alert('Upload failed: ' + (e.response?.data?.error || e.message));
    } finally {
      setUploading(false);
    }
  };

  const filtered = (ehrRecords || []).filter(r => {
    if (filterType === 'ALL') return true;
    return r.recordType === filterType;
  });

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-4xl max-h-[85vh] flex flex-col shadow-2xl overflow-hidden">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-lg bg-cyan-950 text-cyan-400 border border-cyan-800">
              <FileText className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white tracking-tight">
                Electronic Health Records (EHR) & Clinical Laboratory Panels
              </h3>
              <p className="text-xs text-slate-400 mt-0.5">
                Authorized hospital health records, LOINC laboratory values, and diagnoses
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

        {/* Upload Section (Step 2 Demo) */}
        <div className="p-6 bg-slate-950/40 border-b border-slate-800">
          <span className="text-xs font-bold text-slate-200 block mb-2 uppercase tracking-wider flex items-center gap-1.5">
            <UploadCloud className="w-4 h-4 text-cyan-400" />
            Import Hospital EHR Export (CSV)
          </span>

          <div className="flex flex-col sm:flex-row items-center gap-3">
            <input
              type="file"
              accept=".csv"
              onChange={handleFileChange}
              className="text-xs text-slate-400 file:mr-3 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-xs file:font-semibold file:bg-slate-800 file:text-cyan-400 hover:file:bg-slate-700 cursor-pointer w-full sm:w-auto"
            />
            <button
              onClick={handleUpload}
              disabled={!file || uploading}
              className="px-4 py-2 bg-cyan-600 hover:bg-cyan-500 disabled:opacity-50 text-white text-xs font-bold rounded-xl transition flex items-center gap-2 cursor-pointer shrink-0"
            >
              {uploading ? 'Validating & Importing...' : 'Validate & Ingest CSV'}
            </button>
          </div>

          {/* Upload Report */}
          {uploadReport && (
            <div className="mt-3 p-3 bg-emerald-950/50 border border-emerald-800/80 rounded-xl text-xs text-emerald-300 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                <span>
                  Successfully imported <strong>{uploadReport.successfulImports}</strong> records from <em>{uploadReport.filename}</em>. Twin recalculation triggered.
                </span>
              </div>
              <span className="text-[11px] font-mono text-emerald-400">Status: PERSISTED</span>
            </div>
          )}
        </div>

        {/* Filter Tabs */}
        <div className="px-6 py-3 border-b border-slate-800 flex items-center gap-2 text-xs">
          {['ALL', 'LAB_RESULT', 'MEDICATION', 'DIAGNOSIS', 'VITAL_SIGN'].map(tab => (
            <button
              key={tab}
              onClick={() => setFilterType(tab)}
              className={`px-3 py-1 rounded-lg font-medium transition ${
                filterType === tab 
                  ? 'bg-slate-800 text-cyan-400 border border-slate-700' 
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              {tab.replace('_', ' ')}
            </button>
          ))}
        </div>

        {/* Records Table */}
        <div className="p-6 flex-1 overflow-y-auto">
          {filtered.length === 0 ? (
            <div className="text-center py-8 text-xs text-slate-500">
              No matching EHR records found.
            </div>
          ) : (
            <div className="border border-slate-800 rounded-xl overflow-hidden">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-950 text-slate-400 border-b border-slate-800 font-mono text-[11px]">
                  <tr>
                    <th className="py-2.5 px-4">Type</th>
                    <th className="py-2.5 px-4">Code</th>
                    <th className="py-2.5 px-4">Description</th>
                    <th className="py-2.5 px-4">Value / Details</th>
                    <th className="py-2.5 px-4">Recorded Date</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/70 text-slate-300">
                  {filtered.map((r, i) => (
                    <tr key={r.id || i} className="hover:bg-slate-800/30 transition">
                      <td className="py-2.5 px-4">
                        <span className="font-mono text-[10px] px-2 py-0.5 rounded bg-slate-950 text-slate-400 border border-slate-800">
                          {r.recordType}
                        </span>
                      </td>
                      <td className="py-2.5 px-4 font-mono text-cyan-400">{r.code || '—'}</td>
                      <td className="py-2.5 px-4 font-medium text-white">{r.description}</td>
                      <td className="py-2.5 px-4">
                        {r.numericValue !== null && r.numericValue !== undefined ? (
                          <span className="font-mono font-bold text-slate-100">
                            {r.numericValue} <span className="font-normal text-slate-400">{r.unit}</span>
                          </span>
                        ) : (
                          <span className="text-slate-400 italic">{r.textValue || '—'}</span>
                        )}
                      </td>
                      <td className="py-2.5 px-4 text-slate-500 font-mono text-[11px]">
                        {new Date(r.recordedAt).toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="px-6 py-3 border-t border-slate-800 bg-slate-950 flex justify-between items-center text-xs text-slate-400">
          <span>FHIR Resource Structure Compliant</span>
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
