package vn.hau.edumate.ui.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import vn.hau.edumate.R;

import vn.hau.edumate.data.enums.UserStatusCode;
import vn.hau.edumate.ui.auth.LoginActivity;
import vn.hau.edumate.ui.home.HomeViewModel;
import vn.hau.edumate.ui.main.MainViewModel;
import vn.hau.edumate.util.ImageUtil;

public class MyAccountEdit extends Fragment {
    Button btnEdit,btnChangeImage,btnLogout,btnDeleteAccount;
    MyAccountViewModel viewModel;
    MainViewModel mainViewModel;
    HomeViewModel homeViewModel;
    EditText edtUsername;
    TextView tvEmail,tvAuthMethod,tvUserId,tvAccountStatus;
    ImageView imgAvatar;
    FrameLayout loadingView;
    LinearLayout linearLayoutAccountEdit;
    List<Uri> selectedImageUri = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MyAccountViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.account_edit, container, false);
        btnEdit = view.findViewById(R.id.btnEditUser);
        imgAvatar = view.findViewById(R.id.imgAvatarAccount);
        btnChangeImage = view.findViewById(R.id.btnChangeAvatar);
        edtUsername = view.findViewById(R.id.edtUsernameAccount);
        loadingView = view.findViewById(R.id.lottieLoading);
        linearLayoutAccountEdit = view.findViewById(R.id.linearLayoutAccountEdit);
        tvEmail = view.findViewById(R.id.tvEmailAccount);
        tvAuthMethod = view.findViewById(R.id.tvAuthMethodAccount);
        tvUserId = view.findViewById(R.id.tvUserIdAccount);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        tvAccountStatus = view.findViewById(R.id.tvAccountStatus);
        homeViewModel.getIsLogout().observe(getViewLifecycleOwner(), isLogout -> {
            if(isLogout) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });
        btnDeleteAccount.setOnClickListener(v -> {

            RequestBody userStatus = RequestBody.create(UserStatusCode.DELETED.name(), MediaType.parse("text/plain"));
            viewModel.updateUser(requireContext(),null, userStatus,null);
            homeViewModel.signOut();

        });
        viewModel.getIsUpdateLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                loadingView.setVisibility(View.VISIBLE);
                linearLayoutAccountEdit.setVisibility(View.GONE);
            } else {
                loadingView.setVisibility(View.GONE);
                linearLayoutAccountEdit.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), userResponse -> {
            edtUsername.setText(userResponse.getUsername());
            tvEmail.setText(userResponse.getEmail());
            String accountStatus = userResponse.getStatus().name();
            // Hiển thị trạng thái
            displayAccountStatus(accountStatus,tvAccountStatus);

            tvAuthMethod.setText(userResponse.getAuthMethod().getMethod()
                    .equals("firebase-google") ? "Google" : "Facebook");
            tvUserId.setText(userResponse.getId());
            Glide.with(requireContext())
                    .load(userResponse.getAvatarUrl())
                    .centerCrop()
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .into(imgAvatar);
        });
        viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // ✅ Khi update thành công => quay về MyAccount
                mainViewModel.setCurrentFragment(new MyAccountEdit());
            }
        });
        viewModel.getCurrentUser(getContext());

        ActivityResultLauncher<PickVisualMediaRequest> pickSingleImageLauncher =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        selectedImageUri.add(uri);
                        imgAvatar.setImageURI(selectedImageUri.get(0));
                    }
                });

        btnChangeImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            } else {
                PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build();
                pickSingleImageLauncher.launch(request);
            }
        });

        btnEdit.setOnClickListener(v -> {
            String userNameEdit = edtUsername.getText().toString();
            RequestBody usernameBody = RequestBody.create(userNameEdit, MediaType.parse("text/plain"));
            if(!selectedImageUri.isEmpty()){
                List<MultipartBody.Part> file =   ImageUtil.getMultipleImageParts(selectedImageUri, requireContext());
                viewModel.updateUser(requireContext(),usernameBody,null,file.get(0));
                selectedImageUri.clear();

            }else{
                viewModel.updateUser(requireContext(),usernameBody,null,null);
            }
        });
        btnLogout.setOnClickListener(v -> {
            homeViewModel.signOut();
        });


        return view;
    }
    private void displayAccountStatus(String status,TextView tvAccountStatus) {

        GradientDrawable background = (GradientDrawable) tvAccountStatus.getBackground();

        switch (status) {
            case "NORMAL":
                tvAccountStatus.setText("Hợp lệ");
                background.setColor(ContextCompat.getColor(requireContext(), R.color.status_normal));
                break;

            case "WARNING_LOW":
                tvAccountStatus.setText("Cảnh báo mức 1");
                background.setColor(ContextCompat.getColor(requireContext(), R.color.status_warning_low));
                tvAccountStatus.setTextColor(getResources().getColor(android.R.color.black)); // Chữ đen cho nền vàng
                break;

            case "WARNING_MEDIUM":
                tvAccountStatus.setText("Cảnh báo mức 2");
                background.setColor(ContextCompat.getColor(requireContext(), R.color.status_warning_medium));
                break;

            case "WARNING_HIGH":
                tvAccountStatus.setText("Cảnh báo mức 3");
                background.setColor(ContextCompat.getColor(requireContext(), R.color.status_warning_high));
                break;

            case "LOCKED":
                tvAccountStatus.setText("Bị khóa");
                background.setColor(ContextCompat.getColor(requireContext(), R.color.status_warning_high));
                break;

            default:
                tvAccountStatus.setText("Không xác định");
                background.setColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray));
        }
    }
}
