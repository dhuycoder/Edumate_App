import os
import hashlib
from typing import Union, List
from langchain.docstore.document import Document
from langchain_chroma import Chroma
from langchain_community.vectorstores import FAISS
from langchain_community.embeddings import HuggingFaceEmbeddings

class VectorDB:
    def __init__(self,
                 documents: List[Document] = None,
                 vector_db: Union[Chroma, FAISS] = Chroma,
                 embedding = HuggingFaceEmbeddings(model_name="VoVanPhuc/sup-SimCSE-VietNamese-phobert-base"),
                 persist_directory: str = "./data"
                 ) -> None:
        self.vector_db_cls = vector_db
        self.embedding = embedding
        self.persist_directory = persist_directory
        self.db = self._load_or_build_db(documents)

    def _load_or_build_db(self, documents):
        # Nếu Chroma thì có thể load bằng persist_directory
        if self.vector_db_cls == Chroma and os.path.exists(self.persist_directory):
            print("🔁 Đang tải vectorstore từ thư mục...")
            db = Chroma(embedding_function=self.embedding, persist_directory=self.persist_directory)
        elif self.vector_db_cls == FAISS and os.path.exists(os.path.join(self.persist_directory, "index.faiss")):
            print("🔁 Đang tải FAISS từ thư mục...")
            db = FAISS.load_local(self.persist_directory, embeddings=self.embedding)
        else:
            print("🚀 Tạo vectorstore mới...")
            db = self.vector_db_cls.from_documents(
                documents=documents or [],
                embedding=self.embedding,
                persist_directory=self.persist_directory
            )
        return db

    def _hash_content(self, text: str) -> str:
        return hashlib.md5(text.encode()).hexdigest()

    def update_vectorstore(self, documents: List[Document]):
        if not documents:
            return

        existing_ids = set()

        if isinstance(self.db, Chroma):
            existing_ids = set(self.db.get()['ids'])
            print("DEBUG:",existing_ids)
        elif isinstance(self.db, FAISS):
            # FAISS không hỗ trợ get id nên dùng hash so sánh
            pass

        new_docs = []
        new_ids = []

        for doc in documents:
            doc_id = self._hash_content(doc.page_content)
            print("DOCSID:", doc_id)
            if doc_id not in existing_ids:
                new_docs.append(doc)
                new_ids.append(doc_id)

        if new_docs:
            print(f"➕ Thêm {len(new_docs)} tài liệu mới vào vectorstore.")
            if isinstance(self.db, Chroma):
                self.db.add_documents(new_docs, ids=new_ids)
                print(new_docs)

            elif isinstance(self.db, FAISS):
                self.db.add_documents(new_docs)
                self.db.save_local(self.persist_directory)
        else:
            print("✅ Không có tài liệu mới để thêm.")

    def get_retriever(self,
                      search_type: str = "similarity",
                      search_kwargs=None):
        if search_kwargs is None:
            search_kwargs = {"k": 10}
        retriever = self.db.as_retriever(
            search_type=search_type,
            search_kwargs=search_kwargs
        )
        return retriever
