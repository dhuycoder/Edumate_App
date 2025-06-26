package vn.hau.edumate.data.api.chatbot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.hau.edumate.data.model.request.ChatbotRequest;
import vn.hau.edumate.data.model.response.ChatbotResponse;

public interface ChatbotApi {
    @POST("/generative_at")
    Call<ChatbotResponse> generativeAt(@Body ChatbotRequest input);
}
