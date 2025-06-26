package vn.hau.edumate.data.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import vn.hau.edumate.data.api.auth.AuthApi;
import vn.hau.edumate.data.model.request.FirebaseTokenRequest;
import vn.hau.edumate.data.model.response.TokenResponse;

public class TokenAuthenticator implements Authenticator {

    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        TokenManager tokenManager = TokenManager.getInstance(context);

        if (tokenManager.isRefreshTokenExpired()) {
            tokenManager.clearTokens();
            return null;
        }

        synchronized (this) {
            if (!tokenManager.isAccessTokenExpired()) {
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                        .build();
            }

            AuthApi authApi = ApiClient.createService(AuthApi.class, context);
            Call<TokenResponse> call = authApi.getRefreshToken(
                    FirebaseTokenRequest.builder()
                            .token(tokenManager.getRefreshToken())
                            .build()
            );

            retrofit2.Response<TokenResponse> refreshResponse = call.execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                tokenManager.saveToken(refreshResponse.body());

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + tokenManager.getAccessToken())
                        .build();
            } else {
                tokenManager.clearTokens();
                return null;
            }
        }
    }
}

