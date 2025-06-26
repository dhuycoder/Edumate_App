# Kết nối và lấy dữ liệu từ PostgreSQL
from sqlalchemy import create_engine
import pandas as pd

# Tạo kết nối đến PostgreSQL
db_config = {
    'host': 'localhost',
    'port': '5431',
    'database': 'edumate',
    'user': 'postgres',
    'password': '12345678'
}
table_name = 'tbl_post'
engine = create_engine(
    f"postgresql://{db_config['user']}:{db_config['password']}@{db_config['host']}:{db_config['port']}/{db_config['database']}"
)

# Truy vấn dữ liệu từ bảng
query = f"SELECT * FROM {table_name}"
df = pd.read_sql(query, engine)

# Chuyển DataFrame thành documents (giả sử có cột 'text' chứa nội dung)
from langchain.docstore.document import Document

documents = [
        Document(
            page_content=f"bài viết tiêu đề: {row['title']} có nội dung là: {row['content']}"
        )
        for _, row in df.iterrows()
    ]

print(documents)