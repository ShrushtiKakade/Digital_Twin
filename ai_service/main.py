"""
FastAPI Microservice for Happiest Healthy Digital Twin AI & Analytics
Port: 8000
"""
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Dict, Any, List, Optional
from datetime import datetime

from services.insights_engine import InsightsEngine
from services.ml_model import CardioRiskModel
from services.simulation import SimulationEngine

app = FastAPI(
    title="Digital Twin AI & Predictive Analytics Service",
    description="Microservice providing trend analysis, ML risk forecasting, and What-If simulations for patient digital twins.",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Pydantic Data Models ---
class WearableRecord(BaseModel):
    id: Optional[int] = None
    metricType: str
    value: float
    unit: str
    recordedAt: str
    dataQuality: Optional[str] = "VALID"

class InsightsRequest(BaseModel):
    patientId: int
    baseline: Optional[Dict[str, Any]] = Field(default_factory=dict)
    wearableHistory: List[Dict[str, Any]] = Field(default_factory=list)

class PredictionRequest(BaseModel):
    patientId: int
    age: Optional[float] = 52.0
    gender: Optional[str] = "MALE"
    systolicBp: Optional[float] = 142.0
    diastolicBp: Optional[float] = 88.0
    restingHr: Optional[float] = 76.0
    sleepHours: Optional[float] = 6.2
    dailySteps: Optional[float] = 5200.0
    medicationAdherence: Optional[float] = 0.75
    totalCholesterol: Optional[float] = 215.0
    serumCreatinine: Optional[float] = 1.05

class SimulationRequest(BaseModel):
    patientId: int
    scenarioName: Optional[str] = "Hypertension Lifestyle Intervention"
    baselineFeatures: Dict[str, Any]
    hypotheticalInputs: Dict[str, Any]

# --- Endpoints ---

@app.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "DigitalTwin-AI-Service",
        "timestamp": datetime.utcnow().isoformat(),
        "activeModels": [
            {
                "version": CardioRiskModel.MODEL_VERSION,
                "target": CardioRiskModel.TARGET_OUTCOME,
                "horizon": f"{CardioRiskModel.HORIZON_DAYS} days"
            }
        ]
    }

@app.post("/analytics/insights")
def generate_insights(req: InsightsRequest):
    try:
        insights = InsightsEngine.generate_insights(req.model_dump())
        return {
            "patientId": req.patientId,
            "totalInsights": len(insights),
            "insights": insights
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Insight generation error: {str(e)}")

@app.post("/analytics/predict")
def predict_cardio_risk(req: PredictionRequest):
    try:
        features = req.model_dump()
        result = CardioRiskModel.predict(features)
        result["patientId"] = req.patientId
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction error: {str(e)}")

@app.post("/analytics/simulate")
def run_simulation(req: SimulationRequest):
    try:
        result = SimulationEngine.run_simulation(
            patient_id=req.patientId,
            scenario_name=req.scenarioName,
            baseline_features=req.baselineFeatures,
            hypothetical_inputs=req.hypotheticalInputs
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Simulation error: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)

