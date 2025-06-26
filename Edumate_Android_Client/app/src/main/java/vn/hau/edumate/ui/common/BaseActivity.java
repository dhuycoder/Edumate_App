package vn.hau.edumate.ui.common;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseActivity extends AppCompatActivity {
    private BaseViewModel baseViewModel;
    private Dialog networkDialog;
    private Dialog rootDialog;
    private boolean isNetworkDialogShowing = false;
    private boolean isRootDialogShowing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo BaseViewModel
        baseViewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        // Theo dõi trạng thái kết nối mạng
        baseViewModel.getNetworkStatus().observe(this, isConnected -> {
            if (!isConnected) {
                showNetworkErrorDialog();
            } else if (isNetworkDialogShowing && networkDialog != null && networkDialog.isShowing()) {
                networkDialog.dismiss();
                isNetworkDialogShowing = false;
            }
        });

        // Theo dõi trạng thái root
        baseViewModel.getIsDeviceRooted().observe(this, isRooted -> {
            if (isRooted) {
                showRootDetectedDialog();
            }
        });
    }

    private void showNetworkErrorDialog() {
        // Đặt cờ trạng thái hiển thị dialog
        isNetworkDialogShowing = true;

        if (networkDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Lỗi kết nối")
                    .setMessage("Không có kết nối internet. Vui lòng kiểm tra lại.")
                    .setCancelable(false)
                    .setPositiveButton("Thử lại", null) // Thiết lập listener sau
                    .setNegativeButton("Đóng", (dialog, which) -> {
                        isNetworkDialogShowing = false;
                        finishAffinity();
                    });
            networkDialog = builder.create();
        }

        // Thiết lập OnClickListener cho nút "Thử lại" mỗi lần hiển thị dialog
        // Cách này đảm bảo không bị ghi đè bởi AlertDialog.Builder
        networkDialog.setOnShowListener(dialogInterface -> {
            Button retryButton = ((AlertDialog) networkDialog).getButton(DialogInterface.BUTTON_POSITIVE);
            retryButton.setOnClickListener(view -> {
                // Kiểm tra lại kết nối khi nhấn "Thử lại"
                baseViewModel.refreshNetworkStatus();

                // Đợi một chút để kết nối được cập nhật
                view.postDelayed(() -> {
                    // Kiểm tra lại trạng thái kết nối
                    if (baseViewModel.getNetworkStatus().getValue() != null &&
                            baseViewModel.getNetworkStatus().getValue()) {
                        // Nếu có kết nối, đóng dialog
                        isNetworkDialogShowing = false;
                        networkDialog.dismiss();
                    } else {
                        // Nếu vẫn không có kết nối, giữ dialog mở
                        // Dialog đã được mở nên không cần hiển thị lại
                    }
                }, 500); // Đợi 500ms để kết nối được cập nhật
            });
        });

        if (!isFinishing() && !networkDialog.isShowing()) {
            networkDialog.show();
        }
    }

    private void showRootDetectedDialog() {
        if (isRootDialogShowing) return;

        isRootDialogShowing = true;
        if (rootDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cảnh báo bảo mật")
                    .setMessage("Thiết bị của bạn đã được root. Ứng dụng không thể chạy trên thiết bị đã root.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        isRootDialogShowing = false;
                        finishAffinity();
                    })
                    .setNegativeButton("Đóng", (dialog, which) -> {
                        isRootDialogShowing = false;
                        finishAffinity();
                    });
            rootDialog = builder.create();
        }

        if (!isFinishing() && !rootDialog.isShowing()) {
            rootDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra kết nối mạng và root mỗi khi Activity trở lại foreground
        baseViewModel.refreshNetworkStatus();
        if (Boolean.TRUE.equals(baseViewModel.getIsDeviceRooted().getValue())) {
            showRootDetectedDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkDialog != null && networkDialog.isShowing()) {
            networkDialog.dismiss();
        }
        if (rootDialog != null && rootDialog.isShowing()) {
            rootDialog.dismiss();
        }
    }
}
