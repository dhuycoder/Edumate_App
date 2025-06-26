package vn.hau.edumate.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import vn.hau.edumate.data.api.chatbot.ChatbotApi;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.request.ChatbotRequest;
import vn.hau.edumate.data.model.response.ChatMessage;
import vn.hau.edumate.data.model.response.ChatbotResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.ChatBotRepostiory;
import vn.hau.edumate.util.ResultCallback;

@Getter
public class SearchAiViewModel extends AndroidViewModel {
    private final MutableLiveData<List<ChatMessage>> _messages = new MutableLiveData<>();
    private final LiveData<List<ChatMessage>> messagesLiveData = _messages;
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final LiveData<Boolean> isLoadingLiveData = _isLoading;
    private final ChatBotRepostiory chatBotRepostiory;

    public SearchAiViewModel(@NonNull Application application) {
        super(application);
        // Khởi tạo danh sách tin nhắn ban đầu
        chatBotRepostiory = new ChatBotRepostiory(ApiClient.createService(ChatbotApi.class,application, SystemConstant.CHATBOT_URL));
        List<ChatMessage> initialMessages = new ArrayList<>();
        initialMessages.add(new ChatMessage("Xin chào! Tôi là trợ lý ảo. Bạn cần giúp gì?", false, false));
        _messages.setValue(initialMessages);
    }

    public void sendMessage(String message) {
        // Thêm tin nhắn người dùng
        List<ChatMessage> currentMessages = _messages.getValue() != null ?
                new ArrayList<>(_messages.getValue()) : new ArrayList<>();
        currentMessages.add(new ChatMessage(message, true, false));
        _messages.setValue(currentMessages);

        // Gọi bot response
        getBotResponse(message);
    }

    private void getBotResponse(String userMessage) {
        _isLoading.setValue(true);
        new Thread(() -> {
            try {
                chatBotRepostiory.generativeResponse(ChatbotRequest.builder().question(userMessage).build(), new ResultCallback<ChatbotResponse>() {
                    @Override
                    public void onSuccess(ChatbotResponse result) {
                        // Thêm tin nhắn bot vào danh sách
                        List < ChatMessage > currentMessages = _messages.getValue();
                        if (currentMessages != null) {
                            List<ChatMessage> newMessages = new ArrayList<>(currentMessages);
                            newMessages.add(new ChatMessage(result.getAnswer(), false, false));
                            _messages.postValue(newMessages);
                            _isLoading.postValue(false);
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        List < ChatMessage > currentMessages = _messages.getValue();
                        if (currentMessages != null) {
                            List<ChatMessage> newMessages = new ArrayList<>(currentMessages);
                            newMessages.add(new ChatMessage("Lỗi từ server,Vui lòng thử lại...", false, false));
                            _messages.postValue(newMessages);
                            _isLoading.postValue(false);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Xử lý lỗi nếu cần
            } finally {
                _isLoading.postValue(false);
            }
        }).start();
    }

    // Thêm phương thức để xử lý loading message
    public void addLoadingMessage() {
        List<ChatMessage> currentMessages = _messages.getValue();
        if (currentMessages != null) {
            List<ChatMessage> newMessages = new ArrayList<>(currentMessages);
            newMessages.add(new ChatMessage("", false, true)); // isUser = false, isLoading = true
            _messages.setValue(newMessages);
        }
    }

    // Xóa loading message khi có kết quả
    public void removeLoadingMessage() {
        List<ChatMessage> currentMessages = _messages.getValue();
        if (currentMessages != null && !currentMessages.isEmpty()) {
            List<ChatMessage> newMessages = new ArrayList<>(currentMessages);
            // Xóa tin nhắn loading cuối cùng nếu có
            if(newMessages.size() > 3){
                if (newMessages.get(newMessages.size() - 2).isLoading()) {
                    newMessages.remove(newMessages.size() - 2);
                    _messages.setValue(newMessages);
                }
            }
        }
    }
}