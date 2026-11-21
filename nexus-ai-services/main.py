from fastapi import FastAPI
from pydantic import BaseModel


app= FastAPI(title="Nexus AI Service")

class ClassifyRequest(BaseModel):
    text:str

class ClassifyResponse(BaseModel):
    priority:str
    confidence:str

@app.get("/health")
def health_check():
    return {"status":"ok"}

@app.post("/classify",response_model=ClassifyResponse)
def classify___ticket(request:ClassifyRequest):
    text_lower=request.text.lower()
    if "urgent" in text_lower or "crash" in text_lower :
        return ClassifyResponse(priority="CRITICAL", confidence=0.95)
    elif "bug" in text_lower or "error" in text_lower:
        return ClassifyResponse(priority="HIGH", confidece=0.85)
    else:
        return ClassifyResponse(priority="MEDIUM", confidence=0.60)
        
