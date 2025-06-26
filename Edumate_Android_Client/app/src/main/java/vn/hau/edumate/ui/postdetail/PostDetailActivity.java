package vn.hau.edumate.ui.postdetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.request.CreateCommentRequest;
import vn.hau.edumate.data.model.request.ReportRequest;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.ui.bottomsheet.BottomSheetReportOrHidePost;
import vn.hau.edumate.ui.bottomsheet.BottomSheetSelectPostDetail;
import vn.hau.edumate.ui.comment.CommentActivity;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.ui.community.CommunityFragment;
import vn.hau.edumate.ui.community.postcreate.PostCreateActivity;
import vn.hau.edumate.ui.image.FullScreenImageActivity;
import vn.hau.edumate.ui.main.MainActivity;
import vn.hau.edumate.ui.main.MainViewModel;
import vn.hau.edumate.util.FireBaseUtil;
import vn.hau.edumate.util.ImageUtil;
import vn.hau.edumate.util.ResultCallback;
import vn.hau.edumate.util.TimeUtils;

public class PostDetailActivity extends BaseActivity implements BottomSheetSelectPostDetail.BottomSheetListener ,
        BottomSheetReportOrHidePost.BottomSheetReportOrHidePostListener {
    private TextView tvTag, tvCreateDate, tvUserName , tvTitle, tvContent,tvLikeCount,tvCommentCount;
    private ImageView imgUser,imgPreviewComment;

    private Button btnLike;
    private PostDetailViewModel viewModel;

    private FlexboxLayout imageContainer;
    private ConstraintLayout notifyNoCommentLayout,previewImageCommentLayout;
    private LinearLayout commentContainer;
    private ImageButton btnMore;
    private EditText contentCommentCreate;
    private List<CommentResponse> commentResponses = new ArrayList<>();
    private Long postId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);

        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
        tvTag = findViewById(R.id.tag_post_detail);
        tvCreateDate = findViewById(R.id.tv_create_date_post_detaiil);
        tvUserName = findViewById(R.id.tv_user_name_post_detaiil);
        tvTitle = findViewById(R.id.tv_tiltle_post_detaiil);
        tvContent = findViewById(R.id.tv_content_post_detaiil);
        imgUser = findViewById(R.id.img_user_post_detail);
        imageContainer = findViewById(R.id.imageContainer);
        tvLikeCount = findViewById(R.id.tv_like_count_post_detail);
        tvCommentCount = findViewById(R.id.tv_comment_count_post_detail);
        btnLike = findViewById(R.id.btn_like_post_detail);
        notifyNoCommentLayout = findViewById(R.id.notify_no_comment_layout);
        ImageButton btnPostComment = findViewById(R.id.btn_send_comment_post);
        commentContainer = findViewById(R.id.commentContainer);
        contentCommentCreate = findViewById(R.id.edt_comment_post);
        ImageButton btnChoseImageComment = findViewById(R.id.btn_add_image_comment_post);
        previewImageCommentLayout = findViewById(R.id.img_preview_container);
        imgPreviewComment = findViewById(R.id.img_comment_post_preview);
        btnMore = findViewById(R.id.btnMore);
        Button btnBack = findViewById(R.id.btn_back_post_detail);
        Button btnCloseImgPreview = findViewById(R.id.btnCloseImagePreview);
        ConstraintLayout commentInputLayoutItem = findViewById(R.id.comment_input_layout_item);
        FrameLayout createCommentLoadingLayout = findViewById(R.id.layoutCreateCommentLoading);
        Observer<Boolean> likeStatusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean likeStatus) {
                Log.d("liveData1", String.valueOf(likeStatus));
                btnLike.setSelected(likeStatus);
            }
        };
        viewModel.getIsCreatePostLoading().observe(this,createPostLoading ->{
            if(createPostLoading){
                commentInputLayoutItem.setVisibility(View.GONE);
                createCommentLoadingLayout.setVisibility(View.VISIBLE);
            }
            else{
                commentInputLayoutItem.setVisibility(View.VISIBLE);
                createCommentLoadingLayout.setVisibility(View.GONE);
            }
        });

        btnCloseImgPreview.setOnClickListener(v -> {
            previewImageCommentLayout.setVisibility(View.GONE);
            viewModel.getImageCommentId().setValue(null);
        });


        viewModel.getLikeStatus().observe(PostDetailActivity.this, likeStatusObserver);



        postId = getIntent().getLongExtra("post_id",0);

        imageContainer.removeAllViews();

        loadDataPostDetail(postId);

        loadCommentByPostId(postId);
        btnBack.setOnClickListener(v -> {
            onFinish();
        });

        btnPostComment.setOnClickListener(v -> {

            viewModel.saveComment(CreateCommentRequest.builder()
                            .content(contentCommentCreate.getText().toString())
                            .imageId(viewModel.getImageCommentId().getValue())
                            .build(),
                    postId,
                    PostDetailActivity.this,
                    new PostDetailViewModel.SaveCallback() {
                @Override
                public void onSuccess(CommentResponse commentResponse) {
                    List<CommentResponse> commentList = new ArrayList<>(Objects.
                            requireNonNull(viewModel.getCommentListLiveData().getValue()));
                    commentList.add(commentResponse);
                    commentContainer.removeAllViews();
                    viewModel.getCommentListLiveData().setValue(commentList);
                    contentCommentCreate.setText("");
                    notifyNoCommentLayout.setVisibility(View.GONE);
                    previewImageCommentLayout.setVisibility(View.GONE);
                    viewModel.getImageCommentId().setValue(null);
                    viewModel.getCommentCount().setValue(viewModel.getCommentCount().getValue()+1);
                }

                @Override
                public void onError(String message) {

                }
            });
        });

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        imgPreviewComment.setImageURI(uri);
                        previewImageCommentLayout.setVisibility(View.VISIBLE);
                        uploadImageToServer(List.of(uri));
                    }
                });


        btnChoseImageComment.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }
    @SuppressLint("SetTextI18n")
    private void  loadCommentByPostId(Long postId){
        viewModel.getCommentListLiveData().observe(this,commentResponses ->{
            for (CommentResponse comment : commentResponses) {
                View commentView = LayoutInflater.from(this).inflate(R.layout.comment_item, commentContainer, false);
                TextView tvContent = commentView.findViewById(R.id.tv_content_comment);
                TextView tvName = commentView.findViewById(R.id.tv_name_comment);
                TextView tvCreateDate = commentView.findViewById(R.id.tv_create_date_comment);
                CircleImageView imgUser = commentView.findViewById(R.id.img_user_comment);
                Button btnLike = commentView.findViewById(R.id.btn_like_comment);
                TextView tvLikeCount = commentView.findViewById(R.id.tv_like_comment_count);
                Button btnComment = commentView.findViewById(R.id.btn_comment);
                TextView tvCommentCount = commentView.findViewById(R.id.tv_comment_count);
                ImageView imgCommentImage = commentView.findViewById(R.id.img_comment_image);
                UserResponse user = comment.getUser();
                tvName.setText(user.getUsername());
                tvCreateDate.setText(TimeUtils.getTimeAgo(comment.getCreatedAt()));
                btnLike.setSelected(comment.isLiked());
                AtomicBoolean isLiked = new AtomicBoolean(comment.isLiked());
                btnLike.setOnClickListener(v -> {
                    viewModel.likeComment(comment.getId(), PostDetailActivity.this,new ResultCallback<Integer>() {
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
                btnComment.setOnClickListener(v -> {
                    Intent intent = new Intent(this, CommentActivity.class);
                    intent.putExtra("comment_id", comment.getId());
                    startActivity(intent);

                });
                if(comment.getImage() != null){
                    Glide.with(commentView.getContext())
                            .load(SystemConstant.BASE_URL+"/image/"+comment.getImage().getId())
                            .skipMemoryCache(true)
                            //.override(800,400)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .fitCenter()
                            .into(imgCommentImage);
                }
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imgUser);


                tvContent.setText(comment.getContent());
                tvCommentCount.setText(comment.getRepliesCount().toString());
                tvLikeCount.setText(comment.getLikes().toString());
                commentContainer.addView(commentView);
            }
        });

        viewModel.getCommentByPostId(postId, new PostDetailViewModel.GetCommentByPostIdCallback() {
            @Override
            public void onSuccess(List<CommentResponse> comments) {
                commentResponses = comments;
                if(commentResponses.isEmpty()){
                    notifyNoCommentLayout.setVisibility(View.VISIBLE);
                }
                else{
                    notifyNoCommentLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String error) {
                notifyNoCommentLayout.setVisibility(View.VISIBLE);
                Toast.makeText(PostDetailActivity.this,error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        imageContainer.removeAllViews();
        commentContainer.removeAllViews();
        viewModel.getPostById(getIntent().getLongExtra("post_id",0));
        viewModel.getCommentByPostId(getIntent().getLongExtra("post_id",0), new PostDetailViewModel.GetCommentByPostIdCallback() {
            @Override
            public void onSuccess(List<CommentResponse> comments) {
                commentResponses = comments;
                if(commentResponses.isEmpty()){
                    notifyNoCommentLayout.setVisibility(View.VISIBLE);
                }
                else{
                    notifyNoCommentLayout.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String error) {
                notifyNoCommentLayout.setVisibility(View.VISIBLE);
                Toast.makeText(PostDetailActivity.this,error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadDataPostDetail(Long postId){
        viewModel.getPostLiveData().observe(this,postResponse ->{
            tvTag.setText(postResponse.getTag().getName());
            tvCreateDate.setText(TimeUtils.getTimeAgo(postResponse.getCreatedAt()));
            tvUserName.setText(postResponse.getAuthor().getUsername());
            tvTitle.setText(postResponse.getTitle());
            tvContent.setText(postResponse.getContent());
            tvLikeCount.setText(postResponse.getLikeCount().toString());
            viewModel.getCommentCount().observe(this,commentCount ->{
                tvCommentCount.setText(String.valueOf(commentCount));
            });
            UserResponse authorPost = postResponse.getAuthor();
            if(Objects.equals(FireBaseUtil.getCurrentUserId(), authorPost.getId())){
                btnMore.setOnClickListener(v -> {
                    BottomSheetSelectPostDetail bottomSheetSelectPostDetail = new BottomSheetSelectPostDetail();
                    bottomSheetSelectPostDetail.show(getSupportFragmentManager(), bottomSheetSelectPostDetail.getTag());
                });
            }
            else{
                btnMore.setOnClickListener(v->{
                    BottomSheetReportOrHidePost bottomSheetReportOrHidePost = new BottomSheetReportOrHidePost();
                    bottomSheetReportOrHidePost.show(getSupportFragmentManager(), bottomSheetReportOrHidePost.getTag());
                });
            }
            Log.d("likeData", Objects.requireNonNull(viewModel.getLikeStatus().getValue()).toString());
            viewModel.getLikeStatus().setValue(postResponse.isLiked());
            btnLike.setOnClickListener(v ->{
                viewModel.likePost(postResponse.getId(),PostDetailActivity.this ,new BaseCommunityViewModel.LikeCallback() {
                    @Override
                    public void onSuccess(int likeCount) {
                        Log.d("liveDataLike:" , String.valueOf(likeCount));
                        // Cập nhật dữ liệu trực tiếp
                        tvLikeCount.setText(String.valueOf(likeCount));

                    }
                    @Override
                    public void onError(String error) {
                        Log.e("HomeWorkSupportViewModel", "Lỗi: " + error);
                    }
                });
            });
            Glide.with(this)
                    .load(postResponse.getAuthor().getAvatarUrl())
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgUser);

            if(!postResponse.getImages().isEmpty()){
                loadImages(postResponse);
            }
        });
        viewModel.getPostById(postId);
    }
    private void loadImages(PostResponse postResponse){
        int imageCount = postResponse.getImages().size();
        ArrayList<String> imageUrls = new ArrayList<>();
        for (ImageResponse image : postResponse.getImages()) {
            imageUrls.add(SystemConstant.BASE_URL + "/image/" + image.getId());
        }
            /*
             FlexboxLayout cha: ROW (mặc định)
            * */
        FlexboxLayout imageContainer = findViewById(R.id.imageContainer);
              /*
                Ảnh 1
                * */
        ImageResponse image1 = postResponse.getImages().get(0);
        ImageView imageView1 = new ImageView(this);

        imageView1.setLayoutParams(new FlexboxLayout.LayoutParams( (imageCount== 1 ? 1020  :500), 450));
        ((FlexboxLayout.LayoutParams) imageView1.getLayoutParams()).setFlexGrow(1f);
        Glide.with(this).load(SystemConstant.BASE_URL + "/image/" + image1.getId())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView1);
        imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.image_crop);
        imageView1.setBackground(drawable);
        imageView1.setClipToOutline(true);
        imageView1.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullScreenImageActivity.class);
            intent.putStringArrayListExtra("imageUrls", imageUrls);
            intent.putExtra("position", 0); // nếu click vào ảnh thứ 1

            startActivity(intent);
        });
        imageContainer.addView(imageView1);

        if(imageCount > 1){
                       /*
                       FlexboxLayout con theo chiều dọc
                       */
            FlexboxLayout rightColumn = new FlexboxLayout(this);
            rightColumn.setFlexDirection(FlexDirection.COLUMN);
            FlexboxLayout.LayoutParams columnParams = new FlexboxLayout.LayoutParams(430, ViewGroup.LayoutParams.MATCH_PARENT);
            columnParams.setFlexGrow(1f);
            rightColumn.setLayoutParams(columnParams);


            int displayCount = Math.min(imageCount, 3); // Chỉ hiển thị tối đa 3 ảnh
                    /*
                    Ảnh 2 & 3
                    */
            for (int i = 1; i < displayCount; i++) {
                ImageResponse image = postResponse.getImages().get(i);

                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                drawable = ContextCompat.getDrawable(this, R.drawable.image_crop);
                imageView1.setBackground(drawable);
                imageView1.setClipToOutline(true);
                Glide.with(this)
                        .load(SystemConstant.BASE_URL + "/image/" + image.getId())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
                // onClickListener cho ảnh
                int finalI = i;
                imageView.setOnClickListener(v->{

                    Intent intent = new Intent(v.getContext(), FullScreenImageActivity.class);
                    intent.putStringArrayListExtra("imageUrls", imageUrls);
                    intent.putExtra("position", finalI); // nếu click vào ảnh thứ i
                    startActivity(intent);

                });
                // Layout params cho từng ảnh
                FlexboxLayout.LayoutParams rightLayoutParams = new FlexboxLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0
                );
                rightLayoutParams.setFlexGrow(1f);
                rightLayoutParams.setMargins(8, 0, 0, 8);

                // Trường hợp bình thường (ảnh 2) và ảnh 3 trong trường hợp chỉ có 3 ảnh
                if (i < 2 || imageCount <= 3) {
                    imageView.setLayoutParams(rightLayoutParams);
                    imageView.setBackground(drawable);
                    imageView.setClipToOutline(true);
                    rightColumn.addView(imageView);
                }
                     /*
                       Trường hợp ảnh thứ 3 khi imageCount nhiều hơn 3 ảnh: thêm FrameLayout + TextView "+N"
                        */
                else {
                    FrameLayout frameLayout = new FrameLayout(this);
                    frameLayout.setLayoutParams(rightLayoutParams);
                    frameLayout.setBackground(drawable);
                    frameLayout.setClipToOutline(true);

                    imageView.setLayoutParams(new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));
                    imageView.setBackground(drawable);
                    imageView.setClipToOutline(true);
                    frameLayout.addView(imageView);

                    // TextView hiển thị "+N"
                    TextView overlayText = new TextView(this);
                    overlayText.setText("+" + (imageCount - 3));
                    overlayText.setTextColor(Color.WHITE);
                    overlayText.setTextSize(24);
                    overlayText.setTypeface(null, Typeface.BOLD);
                    overlayText.setGravity(Gravity.CENTER);
                    overlayText.setBackgroundColor(Color.parseColor("#80000000")); // nửa trong suốt

                    FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    );
                    overlayText.setLayoutParams(textParams);

                    frameLayout.addView(overlayText);
                    rightColumn.addView(frameLayout);
                }
            }
            imageContainer.addView(rightColumn);
        }
    }
    private void uploadImageToServer ( List<Uri> uris) {
        List<MultipartBody.Part> parts = ImageUtil.getMultipleImageParts(uris, this);
        viewModel.saveImage(parts,PostDetailActivity.this);
    }



    @Override
    public void onEditClicked() {
            Intent intent = new Intent(this, PostCreateActivity.class);
            intent.putExtra("post", viewModel.getPostLiveData().getValue());
            startActivity(intent);
            finish();
    }

    @Override
    public void onDeleteClicked() {
        viewModel.deletePost(postId, new ResultCallback<>() {

            @Override
            public void onSuccess(DataResponse<PostResponse> result) {
                onFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(PostDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onReportClicked() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_report, null);
        RadioGroup radioGroup = view.findViewById(R.id.report_reason_group);
        Button btnReport = view.findViewById(R.id.btn_report);

        btnReport.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selected = view.findViewById(selectedId);
                String reason = selected.getText().toString();
                // TODO: Gửi tố cáo
                viewModel.reportPost(postId, ReportRequest.builder()
                        .reason(reason)
                        .build(), new ResultCallback<>() {
                    @Override
                    public void onSuccess(DataResponse<Void> result) {
                        Toast.makeText(PostDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Toast.makeText(PostDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng chọn lý do", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    @Override
    public void onHideClicked() {
        viewModel.hidePost(postId, new ResultCallback<>() {

            @Override
            public void onSuccess(DataResponse<Void> result) {
                Toast.makeText(PostDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                onFinish();
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(PostDetailActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onFinish(){
        Intent intent = new Intent(PostDetailActivity.this, MainActivity.class);
        intent.putExtra("navigate_to_fragment", "CommunityFragment"); // Hoặc tên bất kỳ bạn tự đặt
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // đóng PostDetailActivity
    }
}