package vn.hau.edumate.data.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.hau.edumate.data.api.chatbot.ChatbotApi;
import vn.hau.edumate.data.constant.SystemConstant;

public class ChatBotClient {

    private static Retrofit retrofit = null;

    public static ChatbotApi getApiService() {
        if (retrofit == null) {



            retrofit = new Retrofit.Builder()
                    .baseUrl(SystemConstant.CHATBOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ChatbotApi.class);
    }
}
