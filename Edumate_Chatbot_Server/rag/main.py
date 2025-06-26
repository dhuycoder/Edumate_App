
from pydantic import BaseModel,Field
from rag.offline_rag import Offline_Rag

class InputQA(BaseModel):
    question: str = Field(..., title="Question to ask the model")


class OutputQA(BaseModel):
    answer: str = Field(..., title="Awnser from the model")



def build_rag_chain(llm,db_config,table_name,vectorDB):
    # doc_loaded = Loader(file_type=data_type).load_dir(data_dir,workers=2)


    # Chuyển DataFrame thành documents (giả sử có cột 'text' chứa nội dung)
    

    retriever = vectorDB.get_retriever()
    rag_chain = Offline_Rag(llm).get_chain(retriever)
    return rag_chain


