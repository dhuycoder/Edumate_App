from langchain_google_genai import ChatGoogleGenerativeAI
import os

def get_hf_llm(model_name: str = "gemini-2.0-flash", **kwargs):
    # Set API Key từ biến môi trường hoặc gán thẳng nếu test
    os.environ["GOOGLE_API_KEY"] = "AIzaSyCAlW2Qz-Csc4nXQ8n5cduNlnjOIjDDJ4A"

    llm = ChatGoogleGenerativeAI(
        model=model_name,
        **kwargs
    )

    return llm
