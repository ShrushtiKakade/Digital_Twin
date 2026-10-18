"""
Simulation Engine for Happiest Healthy Digital Twin
Runs counterfactual 'What-If' scenarios without mutating real medical records.
Compares baseline predictions vs hypothetical scenario interventions.
"""
from typing import Dict, Any
from copy import deepcopy
from datetime import datetime
from services.ml_model import CardioRiskModel

class SimulationEngine:
    SCIENTIFIC_DISCLAIMER = (
        "CRITICAL SCIENTIFIC LIMITATION: This predictive scenario demonstrates the mathematical "
        "response of the risk model to hypothetical parameter inputs. It does NOT automatically "
        "prove that changing sleep or physical activity will cause this exact clinical improvement. "
        "Causal treatment effects require validated interventional trials or formal causal inference."
    )

    @classmethod
    def run_simulation(
        cls,
        patient_id: int,
        scenario_name: str,
        baseline_features: Dict[str, Any],
        hypothetical_inputs: Dict[str, Any]
    ) -> Dict[str, Any]:
        """
        Executes counterfactual simulation comparing baseline vs hypothetical features.
        """
        # 1. Compute Baseline Risk
        baseline_pred = CardioRiskModel.predict(baseline_features)

        # 2. Construct Hypothetical Feature Copy (Zero Mutation of Baseline)
        simulated_features = deepcopy(baseline_features)

        # Apply supported overrides
        if "sleepHours" in hypothetical_inputs and hypothetical_inputs["sleepHours"] is not None:
            simulated_features["sleepHours"] = float(hypothetical_inputs["sleepHours"])
        if "dailySteps" in hypothetical_inputs and hypothetical_inputs["dailySteps"] is not None:
            simulated_features["dailySteps"] = float(hypothetical_inputs["dailySteps"])
        if "medicationAdherence" in hypothetical_inputs and hypothetical_inputs["medicationAdherence"] is not None:
            simulated_features["medicationAdherence"] = float(hypothetical_inputs["medicationAdherence"])
        if "systolicBp" in hypothetical_inputs and hypothetical_inputs["systolicBp"] is not None:
            simulated_features["systolicBp"] = float(hypothetical_inputs["systolicBp"])
        if "restingHr" in hypothetical_inputs and hypothetical_inputs["restingHr"] is not None:
            simulated_features["restingHr"] = float(hypothetical_inputs["restingHr"])

        # 3. Compute Simulated Scenario Risk
        simulated_pred = CardioRiskModel.predict(simulated_features)

        baseline_risk = baseline_pred["estimatedRisk"]
        simulated_risk = simulated_pred["estimatedRisk"]
        delta_risk = round(simulated_risk - baseline_risk, 4)
        pct_relative_change = round(((simulated_risk - baseline_risk) / baseline_risk) * 100.0, 1) if baseline_risk > 0 else 0.0

        return {
            "patientId": patient_id,
            "scenarioName": scenario_name or "Custom Clinical Scenario",
            "hypotheticalInputs": hypothetical_inputs,
            "baselineFeatures": baseline_features,
            "simulatedFeatures": simulated_features,
            "baselineRisk": {
                "estimatedRisk": baseline_risk,
                "riskPercentage": baseline_pred["riskPercentage"],
                "riskTier": baseline_pred["riskTier"],
                "confidenceInterval": baseline_pred["confidenceInterval"]
            },
            "simulatedRisk": {
                "estimatedRisk": simulated_risk,
                "riskPercentage": simulated_pred["riskPercentage"],
                "riskTier": simulated_pred["riskTier"],
                "confidenceInterval": simulated_pred["confidenceInterval"]
            },
            "comparison": {
                "absoluteDeltaRisk": delta_risk,
                "relativePercentageChange": pct_relative_change,
                "direction": "REDUCED_RISK" if delta_risk < 0 else ("INCREASED_RISK" if delta_risk > 0 else "NO_CHANGE"),
                "summary": (
                    f"Simulated intervention yields a {abs(pct_relative_change):.1f}% "
                    f"{'reduction' if delta_risk < 0 else 'increase'} in 30-day exacerbation risk "
                    f"({baseline_pred['riskPercentage']}% -> {simulated_pred['riskPercentage']}%)."
                )
            },
            "scientificDisclaimer": cls.SCIENTIFIC_DISCLAIMER,
            "simulatedAt": datetime.utcnow().isoformat()
        }

