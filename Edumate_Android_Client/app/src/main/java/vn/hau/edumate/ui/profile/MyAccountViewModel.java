package vn.hau.edumate.ui.profile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import lombok.Getter;
import lombok.Setter;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import vn.hau.edumate.data.api.image.ImageApi;
import vn.hau.edumate.data.api.user.UserApi;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.data.network.ApiClient;

import vn.hau.edumate.data.repository.UserRepository;
import vn.hau.edumate.util.ResultCallback;

@Getter
public class MyAccountViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<UserResponse> _userLiveData = new MutableLiveData<>();
    private final LiveData<UserResponse> userLiveData = _userLiveData;
    private final MutableLiveData<Boolean> _updateSuccess = new MutableLiveData<>();
    private final LiveData<Boolean> updateSuccess = _updateSuccess;
    private final MutableLiveData<Boolean> _isUpdateLoading = new MutableLiveData<>();
    private final LiveData<Boolean> isUpdateLoading = _isUpdateLoading;
    private final MutableLiveData<Boolean> _isAccountDataLoading = new MutableLiveData<>();
    private final LiveData<Boolean> isAccountDataLoading = _isAccountDataLoading;

    public MyAccountViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(ApiClient.createService(UserApi.class,application));
    }
    public void updateUser(Context context, RequestBody username, RequestBody status,MultipartBody.Part file){
        _isUpdateLoading.postValue(true);
        userRepository.updateUser(context, username,status,file, new ResultCallback<DataResponse<UserResponse>>() {

            @Override
            public void onSuccess(DataResponse<UserResponse> result) {

                if(result.getData() != null){
                    _userLiveData.setValue(result.getData());
                    _updateSuccess.setValue(true);
                    _isUpdateLoading.postValue(false);
                }else{
                    _updateSuccess.setValue(false);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("MyAccountViewModel", "Lỗi: " + throwable.getMessage());
                _updateSuccess.setValue(false);
            }
        });
    }
    public void getCurrentUser(Context context){
        _isAccountDataLoading.postValue(true);
        _isUpdateLoading.postValue(true);
        userRepository.getCurrentUser(context, new ResultCallback<DataResponse<UserResponse>>() {
            @Override
            public void onSuccess(DataResponse<UserResponse> result) {
                _userLiveData.setValue(result.getData());
                _isAccountDataLoading.postValue(false);
                _isUpdateLoading.postValue(false);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("MyAccountViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }

}
