package vn.hau.edumate.ui.camera;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import vn.hau.edumate.data.enums.SearchMode;
import vn.hau.edumate.data.enums.UserStatusCode;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.databinding.ActivityCameraBinding;
import vn.hau.edumate.ui.auth.LoginActivity;
import vn.hau.edumate.ui.bottomsheet.TalkTypeBottomSheet;
import vn.hau.edumate.ui.camera.ai.AIResultActivity;
import vn.hau.edumate.ui.camera.semantic.SemanticLoadingActivity;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.ui.community.postcreate.PostCreateActivity;
import vn.hau.edumate.ui.home.HomeViewModel;


public class CameraActivity extends BaseActivity {
    private static final String TAG = "CameraActivity";
    private ActivityCameraBinding binding;
    private CameraViewModel viewModel;
    private HomeViewModel homeViewModel;
    private SearchMode currentMode = SearchMode.AI_SEARCH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupTabListeners();
        viewModel = new ViewModelProvider(this).get(CameraViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupObservers();
        viewModel.startCamera(this);
        homeViewModel.getIsLogout().observe(this, isLogout -> {
            if(isLogout) {
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
            }
        });
        Observer<DataResponse<UserResponse>> observer = new Observer<DataResponse<UserResponse>>() {
            @Override
            public void onChanged(DataResponse<UserResponse> userResponse) {
                // Remove observer sau khi nhận dữ liệu
                if (userResponse.getData().getStatus() != UserStatusCode.NORMAL) {
                    if (userResponse.getData().getStatus() == UserStatusCode.LOCKED) {
                        new AlertDialog.Builder(CameraActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Bạn đã bị khóa tài khoản.Vui lòng liên hệ quản trị viên để hỗ trợ")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .setCancelable(true)
                                .show();
                        return;
                    }

                    String expiredAtString = userResponse.getData().getExpiredAt();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
                    LocalDateTime expiredAt = LocalDateTime.parse(expiredAtString, formatter);

                    if (expiredAt.isAfter(LocalDateTime.now())) {
                        Duration remaining = Duration.between(LocalDateTime.now(), expiredAt);

                        long days = remaining.toDays();
                        long hours = remaining.toHours() % 24;
                        long minutes = remaining.toMinutes() % 60;

                        String remainingStr = String.format("%d ngày, %d giờ, %d phút", days, hours, minutes);

                        new AlertDialog.Builder(CameraActivity.this)
                                .setTitle("Thông báo")
                                .setMessage(String.format(
                                        "Tài khoản đang bị cảnh báo (%s). " +
                                                "Vui lòng chờ đến khi hết hạn (còn lại: %s) hoặc liên hệ hỗ trợ.",
                                        userResponse.getData().getStatus().getDescription(), remainingStr))
                                .setPositiveButton("OK", (dialog, which) -> {
                                    homeViewModel.signOut(); // đăng xuất luôn khi ấn OK
                                    dialog.dismiss();

                                })
                                .setCancelable(true)
                                .show();
                    }
                } else {
                    binding.captureButton.setEnabled(false);
                    binding.captureButton.setAlpha(0.5f);
                    viewModel.takePhoto(CameraActivity.this);
                    showFlashEffect();
                }
            }
        };

        // Gắn observer
        viewModel.getUserLiveData().observe(this, observer);

        binding.captureButton.setOnClickListener(v -> {
            // Gọi API
            viewModel.getCurrentUser(v.getContext());
        });

        binding.galleryButton.setOnClickListener(v -> pickImageFromGallery());
        binding.closeButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.pauseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.resumeCamera(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (viewModel != null) {
            viewModel.releaseCamera();
        }
        if (binding != null) {
            binding = null;
        }
    }

    private void setupObservers() {
        viewModel.getCameraProvider().observe(this, cameraProvider -> {
            if (cameraProvider != null && !isFinishing()) {
                viewModel.bindCameraUseCases(this, cameraProvider, binding.previewView);
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !isFinishing()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCapturedImageUri().observe(this, uri -> {
            if (uri != null && !isFinishing()) {
                startCrop(uri);
            }
            binding.captureButton.setEnabled(true);
            binding.captureButton.setAlpha(1.0f);
        });
    }

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && !isFinishing()) {
                    startCrop(uri);
                }
            });

    private void pickImageFromGallery() {
        pickImageLauncher.launch("image/*");
    }

    private void startCrop(Uri sourceUri) {
        try {
            if (sourceUri == null) {
                Toast.makeText(this, "URI ảnh không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));

            UCrop.Options options = new UCrop.Options();
            options.setToolbarTitle("Cắt ảnh");
            options.setFreeStyleCropEnabled(true);
            options.setShowCropGrid(true);
            options.setHideBottomControls(false);
            options.setCompressionQuality(100);

            UCrop uCrop = UCrop.of(sourceUri, destinationUri)
                    .withOptions(options);

            uCrop.start(this);
            Log.d(TAG, "UCrop started with source: " + sourceUri);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi động UCrop", e);
            Toast.makeText(this, "Không thể mở trình cắt ảnh: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK && data != null) {
                final Uri resultUri = UCrop.getOutput(data);
                Log.d(TAG, "UCrop result URI: " + (resultUri != null ? resultUri.toString() : "null"));

                if (resultUri != null) {

                    navigateToScreenMode(resultUri);
                } else {
                    Toast.makeText(this, "Không nhận được kết quả từ trình cắt ảnh",
                            Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
                final Throwable cropError = UCrop.getError(data);
                Log.e(TAG, "Lỗi khi cắt ảnh", cropError);
                Toast.makeText(this, "Lỗi khi cắt ảnh: " +
                                (cropError != null ? cropError.getMessage() : "Unknown"),
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Người dùng hủy cắt ảnh");
            }
        }
    }

    private void setupTabListeners() {
        binding.tabSearch.setOnClickListener(v -> {
            switchToMode(SearchMode.SEMANTIC_SEARCH);
        });

        binding.tabAiSolver.setOnClickListener(v -> {
            switchToMode(SearchMode.AI_SEARCH);
        });
    }

    @SuppressLint("SetTextI18n")
    private void switchToMode(SearchMode mode) {
        if (mode == currentMode) return;

        currentMode = mode;

        if (mode == SearchMode.SEMANTIC_SEARCH) {
            binding.tabSearch.setAlpha(1f);
            binding.tabAiSolver.setAlpha(0.5f);
            binding.searchTabContent.setVisibility(View.VISIBLE);
            binding.aiSolverTabContent.setVisibility(View.GONE);
            binding.actionText.setText("Tìm kiếm câu hỏi tương tự");
        } else {
            binding.tabSearch.setAlpha(0.5f);
            binding.tabAiSolver.setAlpha(1f);
            binding.searchTabContent.setVisibility(View.GONE);
            binding.aiSolverTabContent.setVisibility(View.VISIBLE);
            binding.actionText.setText("Đặt câu hỏi cho A.I về mọi môn học!");
        }
        vibrateDevice();

        Log.d("SearchMode", "Switched to: " + currentMode.name());
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) return;

        VibrationEffect effect = VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE);

        VibrationAttributes attrs = new VibrationAttributes.Builder()
                .setUsage(VibrationAttributes.USAGE_TOUCH)
                .build();
        vibrator.vibrate(effect, attrs);
    }

    private void showFlashEffect() {
        if (binding != null) {
            binding.flashOverlay.setVisibility(View.VISIBLE);
            binding.flashOverlay.setAlpha(0.8f);
            binding.flashOverlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        if (binding != null) {
                            binding.flashOverlay.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private void navigateToScreenMode(Uri resultUri) {
        if(currentMode == SearchMode.AI_SEARCH){
            try {
                Intent intent = new Intent(this, AIResultActivity.class);
                intent.putExtra("resultUri", resultUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi navigate tới AIResultActivity", e);
                Toast.makeText(this, "Không thể mở màn hình kết quả: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            try {
                Intent intent = new Intent(this, SemanticLoadingActivity.class);
                intent.putExtra("resultUri", resultUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi navigate tới SemanticLoadingActivity", e);
                Toast.makeText(this, "Không thể mở màn hình kết quả: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}