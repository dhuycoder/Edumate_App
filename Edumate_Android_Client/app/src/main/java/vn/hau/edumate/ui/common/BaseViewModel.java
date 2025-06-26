package vn.hau.edumate.ui.common;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BaseViewModel extends AndroidViewModel {
    private DeviceUtils deviceUtils;
    private LiveData<Boolean> networkStatus;
    private MutableLiveData<Boolean> isDeviceRooted = new MutableLiveData<>();

    public BaseViewModel(Application application) {
        super(application);
        deviceUtils = DeviceUtils.getInstance(application);
        networkStatus = deviceUtils.getNetworkStatusLiveData();
        checkDeviceRooted();
    }

    private void checkDeviceRooted() {
        isDeviceRooted.setValue(DeviceUtils.isDeviceRooted());
    }

    public LiveData<Boolean> getNetworkStatus() {
        return networkStatus;
    }

    public LiveData<Boolean> getIsDeviceRooted() {
        return isDeviceRooted;
    }

    public void refreshNetworkStatus() {
        deviceUtils.isNetworkConnected();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
