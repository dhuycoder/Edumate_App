package vn.hau.edumate.ui.camera.semantic;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.databinding.ActivitySemanticResultBinding;
import vn.hau.edumate.ui.postdetail.PostDetailActivity;

public class SemanticResultActivity extends AppCompatActivity {

    private static final String TAG = SemanticResultActivity.class.getSimpleName();

    private SemanticResultViewModel viewModel;
    private ActivitySemanticResultBinding binding;
    private SemanticSearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySemanticResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setRecyclerView();
        viewModel = new ViewModelProvider(this).get(SemanticResultViewModel.class);
        observerData();
        viewModel.getListResult(getIntent());
        viewModel.getUri(getIntent());
        viewModel.getResponse(getIntent());

        binding.btnBack.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setRecyclerView() {
        adapter = new SemanticSearchResultAdapter(this);
        binding.root.setLayoutManager(new LinearLayoutManager(this));
        binding.root.setAdapter(adapter);
        adapter.setOnItemClickListener(this::showSimilarPost);
    }

    @SuppressLint("SetTextI18n")
    private void observerData() {
        viewModel.getSemanticSearchResultsLiveData().observe(this, r -> {
            if (r != null && !r.isEmpty()) {
                adapter.setSemanticSearchResponses(r);
                binding.sumResult.setText("🎉 Tìm thấy " + r.size() + " kết quả");
            }
        });

        viewModel.getPostIdLiveData().observe(this, postId -> {
            if (postId != null) {
                Log.d("SemanticResultActivity", "Received postId: " + postId);
                Intent intent = new Intent(SemanticResultActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", postId);
                startActivity(intent);
            }
        });

        viewModel.getImageUriLiveData().observe(this, uri -> {
            if (uri != null) {
                binding.img.setImageURI(uri);
            } else {
                binding.img.setImageResource(R.drawable.ic_img_error);
            }
        });

        viewModel.getSemanticSearchResponseLiveData().observe(this, semanticSearchResponse -> {
            if (semanticSearchResponse != null) {
                Log.d(TAG, "Received semantic search response: " + semanticSearchResponse);
                Glide.with(this)
                        .load(SystemConstant.BASE_URL + semanticSearchResponse.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_img_error)
                        .into(binding.img);
            } else {
                Log.e(TAG, "No semantic search response found in intent extras.");
            }
        });
    }

    private void showSimilarPost(SemanticSearchResponse semanticSearchResponse) {
        Log.d("SemanticResultActivity", "Showing id image before fetch: " + semanticSearchResponse.getId());
        viewModel.fetchPostId(semanticSearchResponse.getId());
    }

}