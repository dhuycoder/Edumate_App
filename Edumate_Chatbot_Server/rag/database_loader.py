
import pandas as pd
from sqlalchemy import create_engine
from langchain.docstore.document import Document
db_config = {
        'host': 'localhost',
        'port': '5431',
        'database': 'edumate',
        'user': 'postgres',
        'password': '12345678'
    }


class DbLoader:
    def __init__(self, table_name):

        self.db_config = db_config
        self.table_name =table_name
        self.engine = create_engine(self._build_connection_string())

    def _build_connection_string(self) -> str:
        """Xây dựng connection string từ db_config"""
        return f"postgresql://{self.db_config['user']}:{self.db_config['password']}@{self.db_config['host']}:{self.db_config['port']}/{self.db_config['database']}"

    def load(self):
        # Query để lấy dữ liệu bài viết và comment
        query = f"""
        SELECT p.*, 
           STRING_AGG(c.content, ' || ') AS comments
        FROM tbl_post p
        LEFT JOIN tbl_comment c ON p.id = c.post_id
        GROUP BY p.id
        """
        df = pd.read_sql(query, self.engine)

        documents = [
            Document(
                page_content=(
                    f"Tiêu đề: {row['title']}\n"
                    f"Nội dung: {row['content']}\n"
                    f"Bình luận: {row['comments'] or 'Không có bình luận'}"
                ),
                metadata={
                    'id': str(row['id']),
                }
            )
            for _, row in df.iterrows()
        ]
        return documents




