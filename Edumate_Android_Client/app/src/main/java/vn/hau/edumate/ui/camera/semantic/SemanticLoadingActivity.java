package vn.hau.edumate.ui.camera.semantic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import vn.hau.edumate.R;
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.databinding.ActivitySemanticLoadingBinding;

public class SemanticLoadingActivity extends AppCompatActivity {

    private ActivitySemanticLoadingBinding binding;
    private SemanticLoadingViewModel viewModel;
    private Uri imageUri;
    private SemanticSearchHistoryResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySemanticLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SemanticLoadingViewModel.class);

        response = getImageResponseFromIntent();
        imageUri = getImageUriFromIntent();

        setupObservers();
        setupClickListeners();
        playAnimation(R.raw.semantic_searching);

        if(response != null) {
            viewModel.semanticSearchPosts(response.getId(), false);
        }
        else {
            viewModel.semanticSearchPosts(imageUri, true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupObservers() {
        viewModel.getSearchingText().observe(this, text -> binding.info.setText(text));

        viewModel.getErrorMessage().observe(this, errorMessage -> {
            showError(errorMessage, R.raw.server_error);
        });

        viewModel.getResponseMessage().observe(this, message -> {
            showError(message, R.raw.not_found);
            binding.btnRetry.setVisibility(View.VISIBLE);
        });

        viewModel.getPostsSearchedResponse().observe(this, results -> {
            if (results != null && !results.isEmpty()) {
                goToResultActivity(results);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnRetry.setOnClickListener(v -> {
            binding.btnRetry.setVisibility(View.GONE);
            binding.btnBack.setVisibility(View.GONE);
            playAnimation(R.raw.semantic_searching);
            if(imageUri != null) {
                viewModel.semanticSearchPosts(imageUri, false);
            }
            else if(response != null) {
                viewModel.semanticSearchPosts(response.getId(), false);
            }
        });
    }

    private void playAnimation(int resId) {
        binding.loading.setAnimation(resId);
        binding.loading.playAnimation();
    }

    private void showError(String message, int animationRes) {
        binding.info.setText(message);
        binding.btnBack.setVisibility(View.VISIBLE);
        playAnimation(animationRes);
    }

    private void goToResultActivity(List<SemanticSearchResponse> results) {
        Intent intent = new Intent(this, SemanticResultActivity.class);
        intent.putParcelableArrayListExtra("imgList", new ArrayList<>(results));
        if(response != null) {
            intent.putExtra("response", (Parcelable) response);
        }
        if(imageUri != null) {
            intent.putExtra("resultUri", imageUri);
        }
        startActivity(intent);
        finish();
    }

    private Uri getImageUriFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("resultUri")) {
            return intent.getParcelableExtra("resultUri");
        }
        return null;
    }

    private SemanticSearchHistoryResponse getImageResponseFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("response")) {
            return intent.getParcelableExtra("response");
        }
        return null;
    }
}
