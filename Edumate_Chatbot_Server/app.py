
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from langserve import add_routes
from base.llm_model import get_hf_llm
from rag.database_loader import DbLoader
from rag.main import build_rag_chain, OutputQA, InputQA
from rag.vectorstore import VectorDB

# 2. Khởi tạo chain
llm = get_hf_llm(temperature=0.01)
# genai_docs = "./data"
# genai_chain = build_rag_chain(llm, data_dir=genai_docs, data_type="pdf")


db_config = {
    'host': 'localhost',
    'port': '5431',
    'database': 'edumate',
    'user': 'postgres',
    'password': '12345678'
}
# Cấu hình các bảng cần tải
table_name ="tbl_post"
documents = DbLoader(table_name).load()
print(documents)
vectorDB = VectorDB(documents = documents)
# 3. Cấu hình FastAPI
app = FastAPI(
    title="LangChain Server",
    version="1.0",
    description="API hỏi đáp sử dụng RAG",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
    expose_headers=["*"],
)

# 4. Endpoint kiểm tra
@app.get("/check")
async def health_check():
    return {'status': "ok"}

# Khai báo biến toàn cục
genai_chain = None

@app.on_event("startup")
def load_initial_chain():
    global genai_chain
    vectordb = vectorDB
    genai_chain = build_rag_chain(llm, db_config, table_name,vectordb)

@app.post("/reload-data")
def reload_chain():
    global genai_chain
    documents = DbLoader(table_name).load()
    vectorDB.update_vectorstore(documents)
    genai_chain = build_rag_chain(llm, db_config, table_name, vectorDB)
    return {"status": "reloaded and chain rebuilt"}

@app.post('/generative_at', response_model=OutputQA)
async def generative_at(inputs: InputQA):
    answer = genai_chain.invoke(inputs.question)
    return {'answer': answer}


# 5. Đăng ký route với LangServe (phiên bản đã sửa)

# uvicorn app:app --host 0.0.0.0 --port 8000 --reload