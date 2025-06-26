package vn.hau.edumate.ui.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import vn.hau.edumate.R;
import vn.hau.edumate.ui.camera.CameraActivity;

public class intro3 extends AppCompatActivity {
    Button btnNext;

    Button btnBackGuide;

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
                        Log.d("MainActivity", "Nhận được hình ảnh: " + imageUri.toString());
                        Toast.makeText(this, "Đã nhận được hình ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro3);
        btnNext = findViewById(R.id.btnNext3);
        btnBackGuide = findViewById(R.id.btnSkip3);

        btnBackGuide.setOnClickListener(v -> {
            finish();
        });

        btnNext.setOnClickListener(v -> {
            checkPermissions();
        });

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
        finish();
        Intent intent = new Intent(this, CameraActivity.class);
        cameraResultLauncher.launch(intent);
    }
}