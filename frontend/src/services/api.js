import axios from 'axios';

const API_BASE = 'http://localhost:8080/api';

const client = axios.create({
  baseURL: API_BASE,
  timeout: 10000,
});

export const api = {
  // Patients
  getPatients: () => client.get('/patients'),
  getPatient: (id) => client.get(`/patients/${id}`),
  registerPatient: (data) => client.post('/patients', data),

  // Twin State
  getCurrentTwin: (patientId) => client.get(`/patients/${patientId}/twin`),
  getTwinHistory: (patientId) => client.get(`/patients/${patientId}/twin/history`),
  recalculateTwin: (patientId) => client.post(`/patients/${patientId}/twin/recalculate`),

  // Wearables & Telemetry
  getLatestReadings: (patientId, limit = 40) => client.get(`/wearables/${patientId}/latest?limit=${limit}`),
  getMetricHistory: (patientId, metric, limit = 30) => client.get(`/wearables/${patientId}/metric/${metric}?limit=${limit}`),
  
  // Simulator
  startSimulator: (patientId = 1, scenario = 'NORMAL') => 
    client.post(`/wearables/simulator/start?patientId=${patientId}&scenario=${scenario}`),
  stopSimulator: () => client.post('/wearables/simulator/stop'),
  getSimulatorStatus: () => client.get('/wearables/simulator/status'),
  triggerSimulatorTick: (patientId = 1, scenario = 'NORMAL') =>
    client.post(`/wearables/simulator/tick?patientId=${patientId}&scenario=${scenario}`),

  // EHR
  getPatientEhr: (patientId) => client.get(`/ehr/${patientId}`),
  importEhrCsv: (patientId, file) => {
    const formData = new FormData();
    formData.append('patientId', patientId);
    formData.append('file', file);
    return client.post('/ehr/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },

  // Insights
  getInsights: (patientId) => client.get(`/patients/${patientId}/insights`),

  // Predictions
  getPredictions: (patientId) => client.get(`/patients/${patientId}/predictions`),
  triggerPrediction: (patientId) => client.post(`/patients/${patientId}/predictions`),

  // Simulations (What-If)
  executeSimulation: (patientId, scenarioName, hypotheticalInputs) =>
    client.post('/simulations', { patientId, scenarioName, hypotheticalInputs }),
  getPatientSimulations: (patientId) => client.get(`/simulations/patient/${patientId}`),

  // Audit Logs
  getAuditLogs: (patientId) => client.get(`/audit/patient/${patientId}`)
};

