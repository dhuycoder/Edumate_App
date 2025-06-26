package vn.hau.edumate.ui.community.postcreate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import okhttp3.MultipartBody;
import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.enums.UserStatusCode;
import vn.hau.edumate.data.model.request.CreatePostRequest;
import vn.hau.edumate.data.model.request.CreateUserStatusHistory;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.data.model.response.PostResponse;

import vn.hau.edumate.helper.GeminiHelper;
import vn.hau.edumate.ui.bottomsheet.BottomSheetSelectTagType;
import vn.hau.edumate.ui.common.BaseActivity;
import vn.hau.edumate.ui.community.BaseCommunityFragment;
import vn.hau.edumate.ui.postdetail.PostDetailActivity;
import vn.hau.edumate.util.FireBaseUtil;
import vn.hau.edumate.util.ImageUtil;
import vn.hau.edumate.util.ResultCallback;
import vn.hau.edumate.util.SimpleTextWatcher;

public class PostCreateActivity extends BaseActivity {
    private TextView tv_tag_name;
    private EditText edtTitle;
    private EditText edtContent;
    private Button btnUploadPost;
    private TextView tvImgCount;
    private PostResponse post;

    private PostCreateViewModel viewModel;
    private  BottomSheetSelectTagType bottomSheetSelectTagType ;
    private LinearLayout imgPreviewLayout;
    private ConstraintLayout postCreateLayout;
    private FrameLayout loadingLayout;
    private final List<Uri> selectedUris = new ArrayList<>();
    private MutableLiveData<Integer> violationCount = new MutableLiveData<>();



    private Long tagId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_create);
        viewModel = new ViewModelProvider(this).get(PostCreateViewModel.class);

        TextView tvTagTypeName = findViewById(R.id.tv_tag_type_name);
        tv_tag_name = findViewById(R.id.tv_tag_name);
        edtTitle = findViewById(R.id.edt_title);
        edtContent = findViewById(R.id.edt_content);
        imgPreviewLayout = findViewById(R.id.layout_img_preview);
        ImageView btnImgPostCreate = findViewById(R.id.btn_img_post_create);
        ConstraintLayout btn_select_tag = findViewById(R.id.btn_tag_name);
        btnUploadPost = findViewById(R.id.btn_upload_post);
        Button btnBack = findViewById(R.id.btn_back_create_post);
        tvImgCount = findViewById(R.id.tv_img_post_create_count);
        postCreateLayout = findViewById(R.id.post_create_layout);
        loadingLayout = findViewById(R.id.loading_create_post);
        post = (PostResponse) getIntent().getSerializableExtra("post");
        violationCount.observe(this, count -> {
            if(count > 3){
                new AlertDialog.Builder(this)
                        .setTitle("Lỗi")
                        .setMessage("Bạn đã vi phạm vượt quá số lần quy định")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                            this.finish(); // Chỉ kết thúc activity sau khi người dùng ấn OK
                        })
                        .show();
            }
            else{
                showErrorDialog(this, "Bạn còn " + (3 - count) + " lần vi phạm, do bài đăng không hợp lệ với quy chuẩn cộng đồng của chúng tôi");
            }
        });
        viewModel.getIsLoading().observe(this, loading -> {
            if(loading){
                loadingLayout.setVisibility(View.VISIBLE);
                postCreateLayout.setVisibility(View.GONE);
            }
            else{
                loadingLayout.setVisibility(View.GONE);
                postCreateLayout.setVisibility(View.VISIBLE);
            }
        });
        edtTitle.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnUploadPost.setEnabled(!s.toString().isEmpty());

            }
        });

        imgPreviewLayout.setPadding(8, 8, 8, 8);
        ActivityResultLauncher<PickVisualMediaRequest> pickMultipleImagesLauncher =
                registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(), uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        if (selectedUris.size() + uris.size() > 10) {
                            Toast.makeText(this, "Chỉ được chọn tối đa 10 ảnh", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectedUris.addAll(uris);
                        tvImgCount.setText(String.valueOf(selectedUris.size()));
                        refreshImagePreviews();
                    }
                });
        if(post!= null){
            edtTitle.setText(post.getTitle());
            edtContent.setText(post.getContent());
            tv_tag_name.setText(post.getTag().getName());
            tagId = post.getTag().getId();
            tvTagTypeName.setText("Edit Post");
            btn_select_tag.setEnabled(false);
            for(ImageResponse imageResponse : post.getImages()){
                Uri uri = Uri.parse(SystemConstant.BASE_URL + "/image/" + imageResponse.getId());
                selectedUris.add(uri);
            }
            refreshImagePreviews();
        }
        else{
            String tagNameEnumConstraint = getIntent().getStringExtra("TagNameEnumConstraint");
            TagType tagType = TagType.valueOf(tagNameEnumConstraint);

            viewModel.getTagListLiveData().observe(this, tagResponses -> {
                bottomSheetSelectTagType  = new BottomSheetSelectTagType((tagName,id) ->{
                    tv_tag_name.setText(tagName);
                    edtContent.setHint("Nhập nội dung vào đây...\nHãy nhập nội dung");
                    tagId = (long) id;
                }, tagResponses);
                bottomSheetSelectTagType.show(getSupportFragmentManager(), bottomSheetSelectTagType.getTag());
            });
            viewModel.getTagByType(tagType,this);
            tvTagTypeName.setText(tagType.getDescription());
            btn_select_tag.setOnClickListener(v -> bottomSheetSelectTagType.show(getSupportFragmentManager(),
                    bottomSheetSelectTagType.getTag()));
        }
        btnUploadPost.setOnClickListener(v -> {
            if(edtTitle.getText().toString().isEmpty()){
                Toast.makeText(this,"Vui lòng nhập tiêu đề",Toast.LENGTH_SHORT).show();
                return;
            }
            if(edtContent.getText().toString().isEmpty()){
                Toast.makeText(this,"Vui lòng nhập nội dung",Toast.LENGTH_SHORT).show();
                return;
            }
            CreatePostRequest createPostRequest = CreatePostRequest.builder()
                    .content(edtContent.getText().toString())
                    .title(edtTitle.getText().toString())
                    .tagId(tagId)
                    .build();
            if(post != null){
                createPostRequest.setId(post.getId());
            }

            String prompt = "Có đoạn văn bản và hình ảnh như trên, hãy kiểm tra:\"" + edtTitle.getText() + " " + edtContent.getText() + "\". " +
                    "Nếu nội dung là là đồi trụy phản cảm thì trả lời là: \"" + "1, WARNING_LOW\", " +
                    "bạo lực, máu me, kích động là: \"" + "1, WARNING_MEDIUM\", " +
                    "phản động, việt nam cộng hòa, liên quan đến chế độ cũ, đối lập chính trị ở việt nam, ảnh sex, tình dục trẻ em là: \"" + "1, WARNING_HIGH\"," +
                    " nếu không thuộc vào các nội dung cấm trên thì thì trả lời là: \"0, NORMAL\".lưu ý: chỉ trả lời nội dung giống với mẫu tôi đưa ở trên và thêm \", Lý do: \" đằng sau kh trả lời thêm bất kỳ ký tự nào khác nữa để cắt chuỗi";

            if(!selectedUris.isEmpty()){
                List<Bitmap> images = new ArrayList<>();
                for (Uri uri : selectedUris) {
                    try {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        images.add(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                viewModel.askGemini(prompt,images,v.getContext(),this);
                viewModel.getAnswer().observe(this, answer -> {
                    Log.d("GeminiAwnser",answer);
                    if(answer.trim().startsWith("1")){
                        CreateUserStatusByGeminiAnswer(answer);
                    }else{
                        uploadImageToServer(selectedUris);
                        Observer<List<Long>> observer =  imageIds -> {
                            createPostRequest.setImageIds(imageIds);
                            viewModel.createPost(createPostRequest,PostCreateActivity.this ,new ResultCallback<>() {
                                @Override
                                public void onSuccess(DataResponse<PostResponse> result) {
                                    Toast.makeText(v.getContext(),"Đăng bài viết thành công!",Toast.LENGTH_SHORT).show();
                                    onPostSuccess(v,result);
                                }

                                @Override
                                public void onError(Throwable throwable) {

                                }
                            });
                        };
                        viewModel.getImageIds().observe(this,observer);
                    }
                });
            }
            else{
                viewModel.askGemini(prompt,null,v.getContext(),this);
                Observer<String> observer = new Observer<String>() {
                    @Override
                    public void onChanged(String answer) {
                        viewModel.getAnswer().removeObserver(this); // Hủy observer sau khi nhận dữ liệu

                        Log.d("GeminiAwnser", answer);
                        if (answer.trim().startsWith("1")) {
                            CreateUserStatusByGeminiAnswer(answer);
                        } else {
                            createPostRequest.setImageIds(null);
                            viewModel.createPost(createPostRequest, PostCreateActivity.this, new ResultCallback<>() {
                                @Override
                                public void onSuccess(DataResponse<PostResponse> result) {
                                    Toast.makeText(PostCreateActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    onPostSuccess(v, result);
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    Log.d("PostCreateViewModel", "Lỗi: " + throwable.getMessage());
                                }
                            });
                        }
                    }
                };

                viewModel.getAnswer().observe(this, observer);

            }
        });


        btnImgPostCreate.setOnClickListener(v ->{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
            else{
                PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build();
                pickMultipleImagesLauncher.launch(request);
            }
        });
        btnBack.setOnClickListener(v -> finish());


    }
    private void uploadImageToServer (List<Uri> uris) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            // Xử lý ảnh trong background thread
            List<MultipartBody.Part> parts = ImageUtil.getMultipleImageParts(uris, PostCreateActivity.this);

            viewModel.saveImage(parts, PostCreateActivity.this);


        });

    }
    private boolean hasMergedPostImages = false;
    private void refreshImagePreviews() {
        imgPreviewLayout.removeAllViews();

        mergePostImagesToSelectedUris(); // Đảm bảo ảnh từ post cũng vào selectedUris
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.image_crop);
        for (Uri uri : selectedUris) {
            FrameLayout frameLayout = new FrameLayout(this);
            FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(200, 200);
            frameParams.setMargins(8, 8, 8, 8);
            frameLayout.setLayoutParams(frameParams);

            // Image
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setBackground(drawable);
            imageView.setClipToOutline(true);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            Log.d("uri: ",uri.toString());
            Glide.with(this)
                    .load(uri)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);

            // Delete button
            ImageButton deleteButton = new ImageButton(this);
            deleteButton.setImageResource(R.drawable.ic_close_rounded);
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setScaleType(ImageView.ScaleType.CENTER);
            FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(
                    60, 60, Gravity.END | Gravity.TOP
            );
            deleteParams.setMargins(4, 4, 4, 4);
            deleteButton.setLayoutParams(deleteParams);

            deleteButton.setOnClickListener(v -> {
                selectedUris.remove(uri);
                refreshImagePreviews();
            });

            frameLayout.addView(imageView);
            frameLayout.addView(deleteButton);
            imgPreviewLayout.addView(frameLayout);
        }

        tvImgCount.setText(String.valueOf(selectedUris.size()));
    }


    private void mergePostImagesToSelectedUris() {
        if (hasMergedPostImages) return;
        if (post != null && post.getImages() != null) {
            for (ImageResponse image : post.getImages()) {
                Uri uri = Uri.parse(SystemConstant.BASE_URL + "/image/" + image.getId());
                if (!selectedUris.contains(uri)) {
                    selectedUris.add(uri);
                }
            }
        }
        hasMergedPostImages = true;
    }

    private void onPostSuccess(View v, DataResponse<PostResponse> result){
        Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
        intent.putExtra("post_id",result.getData().getId());
        startActivity(intent);
        finish();
    }
    private void createUserStatus(UserStatusCode statusCode, String reason){

        viewModel.createUserStatusHistory(FireBaseUtil.getCurrentUserId(), CreateUserStatusHistory.builder()
                .reason(reason)
                .statusCode(statusCode)
                .build(),new ResultCallback<>() {
            @Override
            public void onSuccess(DataResponse<Integer> result) {
                violationCount.setValue(result.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("PostCreateViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    private void CreateUserStatusByGeminiAnswer(String answer) {
        // Tìm vị trí bắt đầu của từ "lý do"
        int index = answer.indexOf("Lý do");
        String reason = "không tìm thấy lý do";
        if (index != -1) {
            // Cắt chuỗi từ vị trí "lý do" trở đi
            reason = answer.substring(index);
        }
        String simplifiedAnswer = answer.contains("1, WARNING_LOW") ? "WARNING_LOW"
                : answer.contains("1, WARNING_MEDIUM") ? "WARNING_MEDIUM"
                : answer.contains("1, WARNING_HIGH") ? "WARNING_HIGH" : "UNKNOWN";
        switch (simplifiedAnswer) {
            case "WARNING_LOW":
                createUserStatus(UserStatusCode.WARNING_LOW,
                        reason);
                break;
            case "WARNING_MEDIUM":
                createUserStatus(UserStatusCode.WARNING_MEDIUM,
                        reason);
                break;
            case "WARNING_HIGH":
                createUserStatus(UserStatusCode.WARNING_HIGH,
                        reason);
                break;
            default:
                Toast.makeText(this, "Lỗi khi kiểm tra thông tin bài viết", Toast.LENGTH_SHORT).show();
                break;
        }

    }
    private static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }
}