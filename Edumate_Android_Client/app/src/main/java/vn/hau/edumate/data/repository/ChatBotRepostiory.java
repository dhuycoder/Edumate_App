package vn.hau.edumate.data.repository;

import retrofit2.Call;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.chatbot.ChatbotApi;
import vn.hau.edumate.data.api.comment.CommentApi;
import vn.hau.edumate.data.model.request.ChatbotRequest;
import vn.hau.edumate.data.model.response.ChatbotResponse;
import vn.hau.edumate.util.ResultCallback;

public class ChatBotRepostiory {
    private final ChatbotApi chatbotApi;

    public ChatBotRepostiory(ChatbotApi chatbotApi) {
        this.chatbotApi = chatbotApi;
    }
    public void generativeResponse(ChatbotRequest prompt, ResultCallback<ChatbotResponse> callback) {
        Call<ChatbotResponse> call = chatbotApi.generativeAt(prompt);
        ApiCaller.executeApiCall(call,null, new ApiCaller.ResultCallback<ChatbotResponse>() {
            @Override
            public void onSuccess(ChatbotResponse response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
}
