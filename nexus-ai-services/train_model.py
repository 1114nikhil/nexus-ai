import pandas as pd
import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report


data = [
    # CRITICAL
    ("urgent system crash server down", "CRITICAL"),
    ("database is unresponsive and failing", "CRITICAL"),
    ("major outage login page not loading", "CRITICAL"),
    ("security breach unauthorized access", "CRITICAL"),
    ("production is completely broken", "CRITICAL"),
    
    # HIGH
    ("payment gateway returning errors", "HIGH"),
    ("cannot export reports to PDF", "HIGH"),
    ("user gets 500 error on checkout", "HIGH"),
    ("feature not working as expected", "HIGH"),
    ("application is very slow today", "HIGH"),
    
    # MEDIUM
    ("how do I change my password", "MEDIUM"),
    ("requesting a new feature for dashboard", "MEDIUM"),
    ("cannot find the settings page", "MEDIUM"),
    ("need help with basic configuration", "MEDIUM"),
    ("billing invoice question", "MEDIUM"),
    
    # LOW
    ("typo in the footer of the website", "LOW"),
    ("suggestion for UI color scheme", "LOW"),
    ("can you add dark mode", "LOW"),
    ("minor text alignment issue", "LOW"),
    ("just saying hello and thanks", "LOW"),
]


df = pd.DataFrame(data,columns=["text","priority"])
X=df["text"]
y=df["priority"]

X_train,X_test,y_train,y_test=train_test_split(X,y,test_size=0.2,random_state=42)

pipeline=Pipeline([
    ('tfidf',TfidfVectorizer(stop_words='english')),
    ('clf',LogisticRegression(random_state=42,max_iter=200))
])

print("Training model...")
pipeline.fit(X_train,y_train)

print("\nEvaluating model o test set:")
y_pred=pipeline.predict(X_test)
print(classification_report(y_test,y_pred,zero_division=0))

joblib.dump(pipeline,'model.pk1')
print("Model saved to model.pk1")