package vn.hau.edumate.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import vn.hau.edumate.R;

import vn.hau.edumate.ui.main.MainViewModel;


public class MyAccount extends Fragment {
    Button btnLogout,btnEdit;

    MainViewModel viewModel;
    TextView tvUserName,tvEmail;
    ImageView imgAvatar;
    MyAccountViewModel myAccountViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        myAccountViewModel = new ViewModelProvider(requireActivity()).get(MyAccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnEdit = view.findViewById(R.id.btn_edit_account);
        tvUserName = view.findViewById(R.id.tv_name_account);
        tvEmail = view.findViewById(R.id.tv_email_account);
        imgAvatar = view.findViewById(R.id.avatar_account);
        LinearLayout myAccountLayout = view.findViewById(R.id.myAccountLayout);
        FrameLayout loadingAccount = view.findViewById(R.id.myAccountLoading);
        myAccountViewModel.getIsAccountDataLoading().observe(getViewLifecycleOwner(),
                isLoading -> {
                    if (isLoading) {
                        loadingAccount.setVisibility(View.VISIBLE);
                        myAccountLayout.setVisibility(View.GONE);
                    } else {
                        loadingAccount.setVisibility(View.GONE);
                        myAccountLayout.setVisibility(View.VISIBLE);
                    }
                });

        myAccountViewModel.getUserLiveData().observe(getViewLifecycleOwner(), userResponse -> {
            tvUserName.setText(userResponse.getUsername());
            tvEmail.setText(userResponse.getEmail());
            Glide.with(requireContext())
                    .load(userResponse.getAvatarUrl())
                    .centerCrop()
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .into(imgAvatar);
        });

        myAccountViewModel.getCurrentUser(getContext());

        btnLogout.setOnClickListener(v -> {

        });
        btnEdit.setOnClickListener(v -> {
            viewModel.setCurrentFragment(new MyAccountEdit());
        });
        return view;
    }
}