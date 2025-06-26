package vn.hau.edumate.ui.main;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Fragment> currentFragment = new MutableLiveData<>();

    public LiveData<Fragment> getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment fragment) {
        currentFragment.setValue(fragment);
    }
}
