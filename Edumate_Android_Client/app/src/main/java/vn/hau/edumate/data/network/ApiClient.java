package vn.hau.edumate.data.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.hau.edumate.data.constant.SystemConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final int TIMEOUT = 30;

    private static Map<String, Retrofit> retrofitInstances = new HashMap<>();

    private static Retrofit createRetrofit(String url, Context context) {

        if (retrofitInstances.containsKey(url)) {
            return retrofitInstances.get(url);
        }

        Gson gson = new GsonBuilder()
                .setDateFormat("dd/MM/yyyy")
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(context))
                .authenticator(new TokenAuthenticator(context))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitInstances.put(url, retrofit);
        return retrofit;
    }

    public static Retrofit getClient(Context context) {
        return createRetrofit(SystemConstant.BASE_URL, context);
    }

    public static Retrofit getClient(Context context, String customUrl) {
        return createRetrofit(customUrl, context);
    }

    public static <T> T createService(Class<T> serviceClass, Context context) {
        return getClient(context).create(serviceClass);
    }

    // Tạo service với custom URL
    public static <T> T createService(Class<T> serviceClass, Context context, String customUrl) {
        return getClient(context, customUrl).create(serviceClass);
    }

    public static void clearCache() {
        retrofitInstances.clear();
    }

    public static void removeFromCache(String url) {
        retrofitInstances.remove(url);
    }
}
