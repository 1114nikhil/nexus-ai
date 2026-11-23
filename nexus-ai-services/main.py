# from fastapi import FastAPI
# from pydantic import BaseModel


# app= FastAPI(title="Nexus AI Service")

# class ClassifyRequest(BaseModel):
#     text:str

# class ClassifyResponse(BaseModel):
#     priority:str
#     confidence:str

# @app.get("/health")
# def health_check():
#     return {"status":"ok"}

# @app.post("/classify",response_model=ClassifyResponse)
# def classify___ticket(request:ClassifyRequest):
#     text_lower=request.text.lower()
#     if "urgent" in text_lower or "crash" in text_lower :
#         return ClassifyResponse(priority="CRITICAL", confidence=0.95)
#     elif "bug" in text_lower or "error" in text_lower:
#         return ClassifyResponse(priority="HIGH", confidece=0.85)
#     else:
#         return ClassifyResponse(priority="MEDIUM", confidence=0.60)
        

from fastapi import FastAPI
from  pydantic import BaseModel
import joblib
import os

app= FastAPI(title="Nexus AI Service")

MODEL_PATH="model.pk1"

if os.path.exists(MODEL_PATH):
    model=joblib.load(MODEL_PATH)
else:
    model=None
    print("Warning: Model not found,Please run train_model.pk1")


class ClassifyRequest(BaseModel):
    text:str

class ClassifyResponse(BaseModel):
    priority:str
    confidence:str

@app.get("/health")
def health_check():
    return {"status":"ok","model_loded":model is not None}

@app.post("/classify",response_model=ClassifyResponse)
def classify___ticket(request:ClassifyRequest):
    if model is None:
        return ClassifyResponse(priority="MEDIUM",confidence=0.0)
    prediction = model.predicate([request.text])[0]
    probabilities = model.predict_proba([request.text])[0]
    confidence = float(max(probabilities))

    return ClassifyResponse(priority=prediction, confidence=round(confidence, 2))