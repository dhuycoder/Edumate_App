package vn.hau.edumate.ui.camera;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import vn.hau.edumate.data.api.user.UserApi;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.UserRepository;
import vn.hau.edumate.util.ResultCallback;

@Getter
public class CameraViewModel extends AndroidViewModel {
    private static final String TAG = "CameraViewModel";
    private final UserRepository userRepository;
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Uri> capturedImageUri = new MutableLiveData<>();
    private final MutableLiveData<DataResponse<UserResponse>> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<ProcessCameraProvider> cameraProviderLiveData = new MutableLiveData<>();

    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private boolean isCameraPaused = false;

    public CameraViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(ApiClient.createService(UserApi.class,application));
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Uri> getCapturedImageUri() {
        return capturedImageUri;
    }

    public LiveData<ProcessCameraProvider> getCameraProvider() {
        return cameraProviderLiveData;
    }

    public void startCamera(Context context) {
        if (cameraProvider != null && !isCameraPaused) {
            cameraProviderLiveData.postValue(cameraProvider);
            return;
        }

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                isCameraPaused = false;
                cameraProviderLiveData.postValue(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Không thể khởi động camera", e);
                errorMessage.postValue("Không thể khởi động camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void pauseCamera() {
        try {
            if (cameraProvider != null) {
                cameraProvider.unbindAll();
                isCameraPaused = true;
                Log.d(TAG, "Camera paused");
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi pause camera", e);
        }
    }

    public void resumeCamera(Context context) {
        if (isCameraPaused && cameraProvider != null) {
            isCameraPaused = false;
            cameraProviderLiveData.postValue(cameraProvider);
            Log.d(TAG, "Camera resumed");
        }
    }

    public void releaseCamera() {
        try {
            if (cameraProvider != null) {
                cameraProvider.unbindAll();
                cameraProvider = null;
                Log.d(TAG, "Camera resources released");
            }
            imageCapture = null;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi giải phóng camera", e);
        }
    }

    public void bindCameraUseCases(Context context, ProcessCameraProvider cameraProvider, PreviewView previewView) {
        try {
            if (context == null || cameraProvider == null || previewView == null) {
                Log.e(TAG, "Invalid parameters for binding camera");
                return;
            }

            Preview preview = new Preview.Builder().build();

            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build();

            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();


            cameraProvider.unbindAll();


            if (context instanceof LifecycleOwner) {
                cameraProvider.bindToLifecycle(
                        (LifecycleOwner) context, cameraSelector, preview, imageCapture
                );

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } else {
                Log.e(TAG, "Context is not a LifecycleOwner");
                errorMessage.postValue("Lỗi: Context không hợp lệ");
            }

        } catch (Exception e) {
            Log.e(TAG, "Không thể bind camera use cases", e);
            errorMessage.postValue("Lỗi camera: " + e.getMessage());
        }
    }

    public void takePhoto(Context context) {
        if (imageCapture == null) {
            Log.e(TAG, "imageCapture is null");
            errorMessage.postValue("Camera chưa sẵn sàng");
            return;
        }

        if (context == null) {
            Log.e(TAG, "context is null");
            errorMessage.postValue("Context không hợp lệ");
            return;
        }

        try {
            File photoFile = new File(context.getCacheDir(), "photo_" + System.currentTimeMillis() + ".jpg");
            Log.d(TAG, "Photo file path: " + photoFile.getAbsolutePath());

            ImageCapture.OutputFileOptions outputOptions =
                    new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            try {
                                Uri photoUri = FileProvider.getUriForFile(
                                        context,
                                        context.getPackageName() + ".provider",
                                        photoFile
                                );
                                Log.d(TAG, "Photo URI: " + photoUri.toString());
                                capturedImageUri.postValue(photoUri);
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi khi xử lý ảnh đã chụp", e);
                                errorMessage.postValue("Lỗi: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e(TAG, "Lỗi khi chụp ảnh", exception);
                            errorMessage.postValue("Chụp ảnh thất bại: " + exception.getMessage());
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "Lỗi không xác định khi chụp ảnh", e);
            errorMessage.postValue("Lỗi: " + e.getMessage());
        }
    }
    public void getCurrentUser(Context context){
        userRepository.getCurrentUser(context,new ResultCallback<DataResponse<UserResponse>>() {
            @Override
            public void onSuccess(DataResponse<UserResponse> result) {
                userLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("BaseCommunityViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        releaseCamera();
    }
}