package vn.hau.edumate.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import vn.hau.edumate.R;
import vn.hau.edumate.databinding.ActivityMainBinding;
import vn.hau.edumate.ui.camera.CameraActivity;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.ui.community.CommunityFragment;

import vn.hau.edumate.ui.community.profile.ProfileFragment;
import vn.hau.edumate.ui.history.BaseHistoryFragment;
import vn.hau.edumate.ui.home.HomeFragment;
import vn.hau.edumate.ui.profile.MyAccount;
import vn.hau.edumate.ui.profile.MyAccountEdit;

public class MainActivity extends BaseActivity {

    private MainViewModel viewModel;

    private ActivityMainBinding binding;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNavigationFromIntent(intent);
    }

    private void handleNavigationFromIntent(Intent intent) {
        if (intent != null && intent.hasExtra("navigate_to_fragment")) {
            String fragmentName = intent.getStringExtra("navigate_to_fragment");

            if ("CommunityFragment".equals(fragmentName)) {
                // Gọi ViewModel hoặc chuyển Fragment tùy cách bạn đang dùng
                viewModel.setCurrentFragment(new CommunityFragment());
            }
            // Xử lý các trường hợp khác nếu cần
        }
    }

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                Boolean storageGranted = result.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false);

                if (cameraGranted != null && cameraGranted && storageGranted != null && storageGranted) {
                    openCameraActivity();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Thông báo")
                            .setMessage("Bạn cần cấp quyền truy cập vào máy ảnh để sử dụng chức năng này")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            });

    private final ActivityResultLauncher<Intent> cameraResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Xử lý hình ảnh đã chụp và cắt ở đây
                        // Ví dụ: hiển thị trong ImageView hoặc lưu vào database
                        Log.d("MainActivity", "Nhận được hình ảnh: " + imageUri.toString());
                        Toast.makeText(this, "Đã nhận được hình ảnh", Toast.LENGTH_SHORT).show();

                        // Thêm code xử lý hình ảnh tại đây
                        // Ví dụ:
                        // binding.previewImage.setVisibility(View.VISIBLE);
                        // binding.previewImage.setImageURI(imageUri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setBinding();
        initViewModel();
        initDefaultFragmentNavigator(viewModel);
        setFragmentNavigator(viewModel);

        binding.fabCamera.setOnClickListener(v -> {
            checkPermissions();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bottomNavigationView.setBackground(null);
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Observer<Fragment> fragmentObserver = fragment ->
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.mainFrame.getId(), fragment)
                        .commit();

        viewModel.getCurrentFragment().observeForever(fragmentObserver);

    }

    private void initDefaultFragmentNavigator(MainViewModel viewModel) {
        viewModel.setCurrentFragment(new HomeFragment());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            openCameraActivity();
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            });
        }
    }

    private void openCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        cameraResultLauncher.launch(intent);
    }

    private void setFragmentNavigator(MainViewModel viewModel) {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                viewModel.setCurrentFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_community) {
                viewModel.setCurrentFragment(new CommunityFragment());
                return true;
            } else if (itemId == R.id.nav_menu) {
                viewModel.setCurrentFragment(new MyAccountEdit());
                return true;
            } else if (itemId == R.id.nav_history) {
                viewModel.setCurrentFragment(new BaseHistoryFragment());
                return true;
            } else {
                return false;
            }
        });
    }
}