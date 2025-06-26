package vn.hau.edumate.ui.bootstrap;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import vn.hau.edumate.data.api.auth.AuthApi;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.network.TokenManager;
import vn.hau.edumate.data.repository.AuthRepository;
import vn.hau.edumate.util.ResultCallback;
import vn.hau.edumate.util.SingleLiveEvent;

public class LauncherViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>();
    //private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private final SingleLiveEvent<String> errorMessage = new SingleLiveEvent<>();

    private final AuthRepository authRepository;

    public LauncherViewModel(@NonNull Application application) {
        super(application);
        Context appContext = application.getApplicationContext();

        AuthApi authApi = ApiClient.createService(AuthApi.class, appContext);
        authRepository = new AuthRepository(authApi, appContext);

        checkAuthenticationStatus();
    }

    /**
     * Kiểm tra trạng thái xác thực của người dùng
     */
    public void checkAuthenticationStatus() {
        //isLoading.setValue(true);
        //isAuthenticated.setValue(false);

        TokenManager tokenManager = TokenManager.getInstance(getApplication());
        String refreshToken = tokenManager.getRefreshToken();

        if (refreshToken == null || tokenManager.isRefreshTokenExpired()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                tokenManager.clearTokens();
                isAuthenticated.setValue(false);
                //isLoading.setValue(false);
            }, 1500);
            return;
        }

        authRepository.checkAuthentication(new ResultCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    isAuthenticated.setValue(result);
                    //isLoading.setValue(false);
                }, 150);
            }

            @Override
            public void onError(Throwable throwable) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (!tokenManager.isAccessTokenExpired()) {
                        isAuthenticated.setValue(true);
                    } else {
                        errorMessage.setValue("Không thể xác thực: " + throwable.getMessage());
                        isAuthenticated.setValue(false);
                    }
                    //isLoading.setValue(false);
                }, 1500);
            }
        });
    }

    public LiveData<Boolean> getIsAuthenticated() {
        return isAuthenticated;
    }

    /*public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }*/

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
