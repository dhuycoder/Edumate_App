package vn.hau.edumate.ui.community.profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import vn.hau.edumate.R;

import vn.hau.edumate.ui.adapter.ViewPagerProfileAdapter;
import vn.hau.edumate.ui.community.CommunityFragment;
import vn.hau.edumate.ui.main.MainViewModel;


public class ProfileFragment extends Fragment {
    TextView tvEmail, tvIntro,tvPostCount,tvCommentCount;
    ProfileViewModel viewModel;
    ImageView avatar;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerProfileAdapter viewPagerAdapter;
    MainViewModel mainViewModel;
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvEmail = view.findViewById(R.id.tv_email);
        tvIntro = view.findViewById(R.id.tv_intro);
        avatar = view.findViewById(R.id.avatar);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);
        toolbar = view.findViewById(R.id.toolbar);
        tvPostCount = view.findViewById(R.id.tv_post_count);
        tvCommentCount = view.findViewById(R.id.tv_comment_count);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            mainViewModel.setCurrentFragment(new CommunityFragment());
        });
        viewPagerAdapter = new ViewPagerProfileAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        viewModel.getCurrentUser(getContext());
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), userResponse -> {
            tvEmail.setText(userResponse.getData().getUsername());
            tvIntro.setText(userResponse.getData().getEmail());
            tvPostCount.setText(String.valueOf("Tổng số câu hỏi: "+userResponse.getData().getPostCount()));
            tvCommentCount.setText(String.valueOf("Tổng số câu trả lời: "+userResponse.getData().getCommentCount()));
            Log.d("ProfileFragment", "Avatar " + userResponse.getData().getAvatarUrl());
            Glide.with(requireContext())
                    .load(userResponse.getData().getAvatarUrl())
                    .centerCrop()
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .into(avatar);
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        return view;

    }
}