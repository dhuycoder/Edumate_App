package vn.hau.edumate.ui.comment;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.request.CreateCommentRequest;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.util.ImageUtil;
import vn.hau.edumate.util.ResultCallback;

public class CommentActivity extends BaseActivity {
    private TextView tvUserName,tvCreateDate,tvContent,tvLikeCount;
    private Button btnLike;
    private ImageView imgUser;
    private ImageView imgCommentDetail;
    private EditText edtContent;
    private ImageView imgPreviewComment;
    private ConstraintLayout previewImageCommentLayout;
    private  CommentViewModel viewModel;
    private LinearLayout commentContainer;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_comment_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        tvUserName = findViewById(R.id.tv_name_comment_detail);
        tvCreateDate = findViewById(R.id.tv_create_date_comment_detail);
        tvContent = findViewById(R.id.tv_content_comment_detail);
        tvLikeCount = findViewById(R.id.tv_like_comment_count_detail);
        btnLike = findViewById(R.id.btn_like_comment_detail);
        Button btnBack = findViewById(R.id.btn_back_comment_detail);
        imgUser = findViewById(R.id.img_user_comment_detail);
        commentContainer = findViewById(R.id.commentContainer_detail);
        imgCommentDetail = findViewById(R.id.img_comment_image_detail);
        previewImageCommentLayout = findViewById(R.id.img_preview_container_comment_detail);
        edtContent = findViewById(R.id.edt_content_replies);
        imgPreviewComment =findViewById(R.id.img_comment_detail_post_preview);
        ImageView imgPostRepliesComment = findViewById(R.id.btn_add_image_replies);
        ImageButton btnSendReplies = findViewById(R.id.btn_send_replies);
        Long commentId = getIntent().getLongExtra("comment_id",0);
        btnSendReplies.setOnClickListener(v -> {
            viewModel.saveReplies(CreateCommentRequest.builder()
                    .content(edtContent.getText().toString())
                    .imageId(viewModel.getImageRepliesId().getValue())
                    .build(),
                    commentId,
                    CommentActivity.this,
                    new CommentViewModel.SaveCallback(){
                @Override
                public void onSuccess(CommentResponse commentResponse) {
                    List<CommentResponse> repliesList = viewModel.getRepliesListLiveData().getValue();
                    repliesList.add(commentResponse);
                    commentContainer.removeAllViews();
                    viewModel.getRepliesListLiveData().setValue(repliesList);
                    edtContent.setText("");
                    previewImageCommentLayout.setVisibility(View.GONE);
                    viewModel.getImageRepliesId().setValue(null);
                }

                @Override
                public void onError(String message) {

                }
            });
        });


        ActivityResultLauncher<PickVisualMediaRequest> pickImageToServer =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        imgPreviewComment.setImageURI(uri);
                        previewImageCommentLayout.setVisibility(View.VISIBLE);
                        uploadImageToServer(List.of(uri));
                    }
                });

        btnBack.setOnClickListener(v -> {
            finish();
        });
        imgPostRepliesComment.setOnClickListener(v ->{
            pickImageToServer.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        viewModel.getCommentLiveData().observe(this,commentResponse ->{
            tvUserName.setText(commentResponse.getUser().getUsername());
            tvCreateDate.setText(commentResponse.getCreatedAt());
            tvContent.setText(commentResponse.getContent());
            tvLikeCount.setText(commentResponse.getLikes().toString());
            AtomicBoolean isLiked = new AtomicBoolean(commentResponse.isLiked());
            Log.d("isLiked",isLiked.toString());
            btnLike.setSelected(isLiked.get());
            btnLike.setOnClickListener(v -> {
                viewModel.likeComment(commentId,CommentActivity.this ,new ResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer result) {
                        tvLikeCount.setText(result.toString());
                        isLiked.set(!isLiked.get());
                        btnLike.setSelected(isLiked.get());
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
            });
            Glide.with(this)
                    .load(commentResponse.getUser().getAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(imgUser);
            if(commentResponse.getImage()!=null){
                Glide.with(this)
                        .load(SystemConstant.BASE_URL+"/image/"+commentResponse.getImage().getId())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(800,400)
                        .into(imgCommentDetail);
            }
        });
        viewModel.getCommentById(commentId);

        loadRepliesByCommentId(commentId);
    }

    @SuppressLint("SetTextI18n")
    private void  loadRepliesByCommentId(Long parentId) {
        viewModel.getRepliesListLiveData().observe(this, commentResponses -> {
            for (CommentResponse comment : commentResponses) {
                View commentView = LayoutInflater.from(this).inflate(R.layout.comment_item, commentContainer, false);
                TextView tvContent = commentView.findViewById(R.id.tv_content_comment);
                TextView tvName = commentView.findViewById(R.id.tv_name_comment);
                TextView tvCreateDate = commentView.findViewById(R.id.tv_create_date_comment);
                CircleImageView imgUser = commentView.findViewById(R.id.img_user_comment);
                Button btnLike = commentView.findViewById(R.id.btn_like_comment);
                TextView tvLikeCount = commentView.findViewById(R.id.tv_like_comment_count);
                ImageView imgCommentImage = commentView.findViewById(R.id.img_comment_image);
                Button btnComment = commentView.findViewById(R.id.btn_comment);
                TextView tvCommentCount = commentView.findViewById(R.id.tv_comment_count);
                btnComment.setVisibility(GONE);
                tvCommentCount.setVisibility(GONE);
                UserResponse user = comment.getUser();
                tvName.setText(user.getUsername());
                tvCreateDate.setText(comment.getCreatedAt());

                if (comment.getImage() != null) {
                    Glide.with(commentView.getContext())
                            .load(SystemConstant.BASE_URL + "/image/" + comment.getImage().getId())
                            .skipMemoryCache(true)
                            .override(800, 400)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .into(imgCommentImage);
                }
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .centerCrop()
                        .into(imgUser);
                AtomicBoolean isLiked = new AtomicBoolean(comment.isLiked());
                btnLike.setSelected(isLiked.get());
                btnLike.setOnClickListener(v ->{
                    viewModel.likeComment(comment.getId(),CommentActivity.this ,new ResultCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer result) {
                            tvLikeCount.setText(result.toString());
                            isLiked.set(!isLiked.get());
                            btnLike.setSelected(isLiked.get());
                        }
                        @Override
                        public void onError(Throwable throwable) {
                            Toast.makeText(CommentActivity.this, "Error liking comment", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

                tvContent.setText(comment.getContent());
                tvLikeCount.setText(comment.getLikes().toString());

                commentContainer.addView(commentView);
            }
        });
        viewModel.getRepliesByParentId(parentId);
    }
    private void uploadImageToServer ( List<Uri> uris) {
        List<MultipartBody.Part> parts = ImageUtil.getMultipleImageParts(uris, this);
        viewModel.saveImage(parts,CommentActivity.this);
    }
}