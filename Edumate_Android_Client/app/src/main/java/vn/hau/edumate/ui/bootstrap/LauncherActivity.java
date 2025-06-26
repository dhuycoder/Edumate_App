package vn.hau.edumate.ui.bootstrap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import vn.hau.edumate.R;
import vn.hau.edumate.ui.auth.LoginActivity;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.ui.main.MainActivity;

public class LauncherActivity extends BaseActivity {

    private static final String TAG = "LauncherActivity";
    private LauncherViewModel viewModel;
    //private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launching);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.launching), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewModel = new ViewModelProvider(this).get(LauncherViewModel.class);

        //progressBar = findViewById(R.id.progressBar);

        setupObservers();
    }

    private void setupObservers() {

        /*viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });*/

        viewModel.getErrorMessage().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });

        viewModel.getIsAuthenticated().observe(this,
                isAuthenticated -> {
            if (isAuthenticated) {
                Log.d(TAG, "Người dùng đã được xác thực, chuyển hướng đến MainActivity");
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
            } else {
                Log.d(TAG, "Người dùng chưa xác thực, chuyển hướng đến LoginActivity");
                startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
            }
            finish();
        });
    }
}
