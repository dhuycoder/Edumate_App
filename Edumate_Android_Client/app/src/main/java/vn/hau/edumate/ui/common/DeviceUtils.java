package vn.hau.edumate.ui.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DeviceUtils {
    private static volatile DeviceUtils instance;
    private MutableLiveData<Boolean> networkStatusLiveData = new MutableLiveData<>();
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private DeviceUtils(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                networkStatusLiveData.postValue(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                networkStatusLiveData.postValue(false);
            }
        };

        // Kiểm tra kết nối mạng ban đầu
        networkStatusLiveData.setValue(isNetworkConnected());

        // Đăng ký callback để theo dõi thay đổi kết nối
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    public static DeviceUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (DeviceUtils.class) {
                if (instance == null) {
                    instance = new DeviceUtils(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void cleanup() {
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public LiveData<Boolean> getNetworkStatusLiveData() {
        return networkStatusLiveData;
    }

    public boolean isNetworkConnected() {
        if (connectivityManager == null) return false;

        boolean isConnected = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            isConnected = capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
        } else {
            isConnected = connectivityManager.getActiveNetworkInfo() != null &&
                    connectivityManager.getActiveNetworkInfo().isConnected();
        }

        boolean finalIsConnected = isConnected;
        mainHandler.post(() -> networkStatusLiveData.postValue(finalIsConnected));

        return isConnected;
    }

    public void checkNetworkAndUpdate() {
        boolean isConnected = isNetworkConnected();
        mainHandler.post(() -> networkStatusLiveData.setValue(isConnected));
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su" };
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod2() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private static boolean checkRootMethod3() {
        return new File("/system/app/Superuser.apk").exists() ||
                new File("/system/app/superuser.apk").exists();
    }
}
