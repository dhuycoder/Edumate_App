package vn.hau.edumate.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.auth.AuthApi;
import vn.hau.edumate.data.model.request.FirebaseTokenRequest;
import vn.hau.edumate.data.model.response.ErrorResponse;
import vn.hau.edumate.data.model.response.TokenResponse;
import vn.hau.edumate.data.network.TokenManager;
import vn.hau.edumate.util.ResultCallback;

public class AuthRepository {
    private static final String TAG = "AuthRepository";
    private final AuthApi authApi;
    private final FirebaseAuth firebaseAuth;
    private final TokenManager tokenManager;

    public AuthRepository(AuthApi authApi, Context context) {
        this.authApi = authApi;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.tokenManager = TokenManager.getInstance(context);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void checkAuthentication(ResultCallback<Boolean> callback) {

        if (tokenManager.getRefreshToken() == null || tokenManager.isRefreshTokenExpired()) {
            callback.onSuccess(false);
            return;
        }

        if (tokenManager.isAccessTokenExpired()) {
            refreshToken(tokenManager.getRefreshToken(), callback);

        } else {
            callback.onSuccess(true);
        }
    }

    private void refreshToken(String refreshToken, ResultCallback<Boolean> callback) {
        FirebaseTokenRequest request = FirebaseTokenRequest.builder()
                .token(refreshToken)
                .build();

        authApi.getRefreshToken(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body());
                    callback.onSuccess(true);
                } else {
                    tokenManager.clearTokens();
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void signInWithGoogle(String idToken, ResultCallback<FirebaseUser> callback) {
        Log.d(TAG, "Processing Google sign-in");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        signInWithCredential(credential, callback);
    }

    public void signInWithFacebook(AccessToken token, ResultCallback<FirebaseUser> callback) {
        Log.d(TAG, "Processing Facebook sign-in");
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential(credential, callback);
    }

    private void signInWithCredential(AuthCredential credential, ResultCallback<FirebaseUser> callback) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        callback.onSuccess(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    public void getFirebaseToken(FirebaseUser user, ResultCallback<String> callback) {
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult().getToken();
                        Log.d(TAG, "Firebase token retrieved, length: " + token.length());
                        callback.onSuccess(token);
                    } else {
                        Log.e(TAG, "Failed to get ID token", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }

    public void authenticateWithServer(String firebaseToken, ResultCallback<TokenResponse> callback) {
        FirebaseTokenRequest request = FirebaseTokenRequest.builder()
                .token(firebaseToken)
                .build();

        authApi.authenticate(request).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<TokenResponse> call, @NonNull Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenManager.saveToken(response.body());
                    Log.d(TAG, "Server authentication successful");
                    Log.d(TAG, "Saved token to SharedPreferences");
                    callback.onSuccess(response.body());
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        ErrorResponse errorResponse = new Gson().fromJson(errorBody, ErrorResponse.class);
                        Log.e(TAG, "Server authentication error: " + errorResponse.getMessage());
                        callback.onError(new Exception(errorResponse.getMessage()));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                        callback.onError(new Exception("Unknown server error"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TokenResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error", t);
                callback.onError(t);
            }
        });
    }

    public void signOut(ResultCallback<Void> callback) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // 1. Gửi request lên server để thu hồi JWT (đã đúng)
        authApi.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.e(TAG, "Logout success");
                    callback.onSuccess(null);
                } else {
                    callback.onError(new Exception("Logout failed"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Logout failed", t);
                callback.onError(t);
            }
        });

        // 2. Xóa Firebase token và token local
        firebaseAuth.signOut();
        tokenManager.clearTokens();

        // 3. Xác định provider để logout đúng
        if (currentUser != null) {
            for (com.google.firebase.auth.UserInfo info : currentUser.getProviderData()) {
                if (FacebookAuthProvider.PROVIDER_ID.equals(info.getProviderId())) {
                    Log.d(TAG, "Logging out from Facebook");
                    LoginManager.getInstance().logOut();
                }
            }
        }
    }

}
