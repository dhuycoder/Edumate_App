from langchain_community.document_loaders import PyPDFLoader
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_chroma import Chroma

from rag.file_loader import Loader

# pdf_url = "D:\Download\DAM_DUC_HUY_CV.pdf"
# pdf_loader = PyPDFLoader(pdf_url)
# pdf_pages = pdf_loader.load()
# print (pdf_pages)
# chunk_size = 300
# chunk_overlap = 0
#
# splitter = RecursiveCharacterTextSplitter(
#     chunk_size = chunk_size,
#     chunk_overlap = chunk_overlap,
#     length_function= len,
#     is_separator_regex=False,
# )
# docs = splitter.split_documents(pdf_pages)
# embeddings = HuggingFaceEmbeddings()
#
# chroma_db = Chroma.from_documents(docs,embedding = embeddings)
#
# query = "- Khởi tạo và xây dựng cấu trúc dự án theo mô hình server-side rendering, áp dụn."
# similar_docs = chroma_db.similarity_search(query,k = 2)
# print(similar_docs)


# import multiprocessing
#
#
# def load_documents():
#     # Đặt tất cả code xử lý đa tiến trình trong hàm này
#     genai_docs = "./data"
#     doc_loaded = Loader().load_dir('./data', workers=2)
#     print(doc_loaded)
#     return doc_loaded
#
#
# if __name__ == '__main__':
#     # Đặt start method cho multiprocessing (quan trọng trên Windows/macOS)
#     multiprocessing.set_start_method('spawn', force=True)
#
#     # Chạy hàm chính
#     documents = load_documents()
from langchain import hub
print(hub.pull("rlm/rag-prompt"))
