package vn.hau.edumate.ui.home;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.hau.edumate.R;
import vn.hau.edumate.ui.adapter.ChatAdapter;

public class SearchAiActivity extends AppCompatActivity {
    private SearchAiViewModel viewModel;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton,backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_ai);
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(SearchAiViewModel.class);
        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        // Ánh xạ view
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        backBtn = findViewById(R.id.btn_back_search_Ai);
        // Xử lý sự kiện gửi tin nhắn
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                messageEditText.setText("");
            }
        });
        backBtn.setOnClickListener(v -> {
            finish();
        });
        // Quan sát danh sách tin nhắn
        viewModel.getMessagesLiveData().observe(this, messages -> {
            adapter = new ChatAdapter(messages, this);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(messages.size() - 1);
        });

// Quan sát trạng thái loading
        viewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            if (isLoading) {
                viewModel.addLoadingMessage();
            } else {
                viewModel.removeLoadingMessage();
            }
        });
    }
}