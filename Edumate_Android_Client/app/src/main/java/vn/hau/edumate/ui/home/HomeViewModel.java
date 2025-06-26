package vn.hau.edumate.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.hau.edumate.data.api.auth.AuthApi;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.AuthRepository;
import vn.hau.edumate.util.ResultCallback;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> isLogout = new MutableLiveData<>();

    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final AuthRepository authRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        AuthApi authApi = ApiClient.createService(AuthApi.class, application);
        authRepository = new AuthRepository(authApi, application);
    }

    public LiveData<Boolean> getIsLogout() {
        return isLogout;
    }

    public LiveData<String> getUserName() {
        if (userName.getValue() == null) {
            userName.setValue(authRepository.getCurrentUser() != null ? authRepository.getCurrentUser().getDisplayName() : "Guest");
        }
        return userName;
    }

    public void signOut() {
        authRepository.signOut(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                isLogout.setValue(true);
            }

            @Override
            public void onError(Throwable throwable) {
                isLogout.setValue(false);
            }
        });
    }
}
