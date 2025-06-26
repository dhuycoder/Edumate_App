package vn.hau.edumate.ui.auth;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseUser;

import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.api.auth.AuthApi;
import vn.hau.edumate.data.model.response.TokenResponse;
import vn.hau.edumate.data.repository.AuthRepository;
import vn.hau.edumate.util.ResultCallback;
import vn.hau.edumate.util.SingleLiveEvent;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private final SingleLiveEvent<TokenResponse> authSuccess = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> authError = new SingleLiveEvent<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        Context appContext = application.getApplicationContext();

        AuthApi authApi = ApiClient.createService(AuthApi.class, appContext);

        authRepository = new AuthRepository(authApi, appContext);
    }

    /**
     * Checks if a user is already logged in
     */
    public void checkCurrentUser() {
        FirebaseUser user = authRepository.getCurrentUser();
        currentUser.setValue(user);
    }

    /**
     * Initiates Google Sign-In process
     */
    public void signInWithGoogle(String idToken) {
        isLoading.postValue(true);

        authRepository.signInWithGoogle(idToken, new ResultCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                currentUser.postValue(user);
                getFirebaseTokenAndAuthenticate(user);
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.postValue(false);
                authError.postValue("Google sign-in failed: " + throwable.getMessage());
            }
        });
    }

    /**
     * Initiates Facebook Sign-In process
     */
    public void signInWithFacebook(AccessToken token) {
        isLoading.setValue(true);

        authRepository.signInWithFacebook(token, new ResultCallback<FirebaseUser>() {
            @Override
            public void onSuccess(FirebaseUser user) {
                currentUser.setValue(user);
                getFirebaseTokenAndAuthenticate(user);
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.setValue(false);
                authError.setValue("Facebook sign-in failed: " + throwable.getMessage());
            }
        });
    }

    /**
     * Gets Firebase token and authenticates with server
     */
    private void getFirebaseTokenAndAuthenticate(FirebaseUser user) {
        authRepository.getFirebaseToken(user, new ResultCallback<String>() {
            @Override
            public void onSuccess(String token) {
                authenticateWithServer(token);
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.setValue(false);
                authError.setValue("Failed to get authentication token: " + throwable.getMessage());
            }
        });
    }

    /**
     * Sends Firebase token to backend server for authentication
     */
    private void authenticateWithServer(String firebaseToken) {
        authRepository.authenticateWithServer(firebaseToken, new ResultCallback<TokenResponse>() {
            @Override
            public void onSuccess(TokenResponse response) {
                isLoading.setValue(false);
                authSuccess.setValue(response);
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.setValue(false);
                authError.setValue(throwable.getMessage());
            }
        });
    }

    /**
     * Signs out the current user
     */
    public void signOut() {
        //authRepository.signOut();
        currentUser.setValue(null);
    }

    // Getters for LiveData objects
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public LiveData<TokenResponse> getAuthSuccess() {
        return authSuccess;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }
}
