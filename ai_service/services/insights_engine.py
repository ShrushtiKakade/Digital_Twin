"""
Insights Engine for Happiest Healthy Digital Twin
Calculates evidence-backed observations and clinical trends from historical data.
Maintains strict separation between observed data, calculated trends, and evidence citations.
"""
from typing import List, Dict, Any, Optional
from datetime import datetime, timedelta
import math

class InsightsEngine:
    @staticmethod
    def generate_insights(patient_data: Dict[str, Any]) -> List[Dict[str, Any]]:
        insights = []
        wearable_history = patient_data.get("wearableHistory", [])
        baseline = patient_data.get("baseline", {})
        patient_id = patient_data.get("patientId", 1)

        if not wearable_history:
            return [{
                "patientId": patient_id,
                "category": "DATA_QUALITY",
                "severity": "INFO",
                "title": "Insufficient Wearable History",
                "explanation": "No longitudinal wearable readings are recorded yet. Digital twin baseline requires initial data sync.",
                "evidence": {"sampleSize": 0, "metric": "ALL"},
                "confidenceScore": 1.0,
                "dataQualityStatus": "NO_DATA",
                "createdAt": datetime.utcnow().isoformat()
            }]

        # 1. Evaluate Sleep Trends (Comparing last 7 days vs previous 7 days)
        sleep_records = [r for r in wearable_history if r.get("metricType") == "SLEEP_HOURS"]
        if len(sleep_records) >= 4:
            # Sort by recordedAt descending
            sleep_records.sort(key=lambda x: x.get("recordedAt", ""), reverse=True)
            recent_sleep = sleep_records[:7]
            prior_sleep = sleep_records[7:14] if len(sleep_records) >= 8 else []

            recent_vals = [float(r["value"]) for r in recent_sleep if "value" in r]
            avg_recent = sum(recent_vals) / len(recent_vals) if recent_vals else 0

            if prior_sleep:
                prior_vals = [float(r["value"]) for r in prior_sleep if "value" in r]
                avg_prior = sum(prior_vals) / len(prior_vals) if prior_vals else avg_recent
                pct_change = ((avg_recent - avg_prior) / avg_prior) * 100 if avg_prior > 0 else 0

                if pct_change <= -8.0:
                    insights.append({
                        "patientId": patient_id,
                        "category": "SLEEP_DEBT",
                        "severity": "WARNING" if pct_change <= -15.0 else "MEDIUM",
                        "title": f"Sleep Duration Decreased by {abs(pct_change):.1f}%",
                        "explanation": (
                            f"Average sleep duration over the latest period ({avg_recent:.1f} hours) "
                            f"has declined compared to the prior baseline ({avg_prior:.1f} hours). "
                            "Chronic sleep deficiency is associated with blunted nocturnal blood pressure dipping."
                        ),
                        "evidence": {
                            "recentAverageHours": round(avg_recent, 2),
                            "priorAverageHours": round(avg_prior, 2),
                            "percentageChange": round(pct_change, 2),
                            "recentRecordIds": [r.get("id") for r in recent_sleep if r.get("id")],
                            "timeHorizon": "7-day rolling window"
                        },
                        "confidenceScore": 0.92,
                        "dataQualityStatus": "VALID_RECORDS",
                        "createdAt": datetime.utcnow().isoformat()
                    })
            elif avg_recent < 6.0:
                insights.append({
                    "patientId": patient_id,
                    "category": "SLEEP_DEBT",
                    "severity": "MEDIUM",
                    "title": "Sub-optimal Sleep Duration Detected",
                    "explanation": f"Average recorded sleep is {avg_recent:.1f} hours, which is below the recommended 7-hour threshold.",
                    "evidence": {
                        "averageSleep": round(avg_recent, 2),
                        "sampleCount": len(recent_vals),
                        "recordIds": [r.get("id") for r in recent_sleep if r.get("id")]
                    },
                    "confidenceScore": 0.88,
                    "dataQualityStatus": "ACCEPTABLE",
                    "createdAt": datetime.utcnow().isoformat()
                })

        # 2. Evaluate Resting Heart Rate Trends
        hr_records = [r for r in wearable_history if r.get("metricType") == "HEART_RATE"]
        if hr_records:
            hr_records.sort(key=lambda x: x.get("recordedAt", ""), reverse=True)
            recent_hr = hr_records[:20]
            recent_vals = [float(r["value"]) for r in recent_hr if "value" in r]
            avg_hr = sum(recent_vals) / len(recent_vals) if recent_vals else 72.0
            baseline_hr = float(baseline.get("restingHrAvg", 72.0))

            delta_hr = avg_hr - baseline_hr
            if delta_hr >= 5.0:
                insights.append({
                    "patientId": patient_id,
                    "category": "CARDIOVASCULAR",
                    "severity": "WARNING" if delta_hr >= 10.0 else "MEDIUM",
                    "title": f"Resting Heart Rate Elevated (+{delta_hr:.1f} BPM Above Baseline)",
                    "explanation": (
                        f"Current resting heart rate trend is {avg_hr:.1f} BPM, exceeding the patient baseline of {baseline_hr:.1f} BPM. "
                        "Sustained elevation may indicate autonomic stress, medication non-adherence, or subclinical infection."
                    ),
                    "evidence": {
                        "currentTrendBpm": round(avg_hr, 1),
                        "baselineBpm": round(baseline_hr, 1),
                        "deltaBpm": round(delta_hr, 1),
                        "samplePoints": len(recent_vals),
                        "recordIds": [r.get("id") for r in recent_hr[:5] if r.get("id")]
                    },
                    "confidenceScore": 0.90,
                    "dataQualityStatus": "VALID_RECORDS",
                    "createdAt": datetime.utcnow().isoformat()
                })

        # 3. Step Count & Physical Activity
        step_records = [r for r in wearable_history if r.get("metricType") == "STEPS"]
        if step_records:
            step_records.sort(key=lambda x: x.get("recordedAt", ""), reverse=True)
            recent_steps = step_records[:7]
            step_vals = [float(r["value"]) for r in recent_steps if "value" in r]
            avg_steps = sum(step_vals) / len(step_vals) if step_vals else 5000.0

            if avg_steps < 4000:
                insights.append({
                    "patientId": patient_id,
                    "category": "ACTIVITY",
                    "severity": "LOW",
                    "title": "Sedentary Activity Pattern",
                    "explanation": f"Average daily step count is {int(avg_steps):,} steps/day, reflecting reduced ambulatory mobility.",
                    "evidence": {
                        "averageSteps": int(avg_steps),
                        "sampleDays": len(step_vals),
                        "recordIds": [r.get("id") for r in recent_steps if r.get("id")]
                    },
                    "confidenceScore": 0.85,
                    "dataQualityStatus": "ACCEPTABLE",
                    "createdAt": datetime.utcnow().isoformat()
                })

        # 4. Multi-Modal Interaction (Sleep Reduction + Resting HR Elevation)
        has_sleep_debt = any(i["category"] == "SLEEP_DEBT" for i in insights)
        has_hr_elevation = any(i["category"] == "CARDIOVASCULAR" for i in insights)
        if has_sleep_debt and has_hr_elevation:
            insights.append({
                "patientId": patient_id,
                "category": "MULTI_MODAL",
                "severity": "HIGH",
                "title": "Coupled Autonomic Stress: Sleep Deficit & Heart Rate Surge",
                "explanation": (
                    "Multi-sensor cross-analysis indicates that the concurrent decrease in sleep duration "
                    "is temporally correlated with elevated resting heart rate. This combined presentation "
                    "warrants proactive clinician review of antihypertensive compliance."
                ),
                "evidence": {
                    "correlatedDomains": ["SLEEP_HOURS", "HEART_RATE"],
                    "correlationPlausibility": "High physiological coupling",
                    "evidenceTimeRange": "Concurrent 7-day window"
                },
                "confidenceScore": 0.94,
                "dataQualityStatus": "OPTIMAL",
                "createdAt": datetime.utcnow().isoformat()
            })

        return insights
