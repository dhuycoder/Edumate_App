package vn.hau.edumate.data.network;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@RequiredArgsConstructor
public class AuthInterceptor implements Interceptor {

    private final Context context;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        TokenManager tokenManager = TokenManager.getInstance(context);
        String token = tokenManager.getAccessToken();

        Request originalRequest = chain.request();

        if (token == null || originalRequest.header("No-Auth") != null) {
            return chain.proceed(originalRequest);
        }

        Request authorisedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(authorisedRequest);
    }
}
