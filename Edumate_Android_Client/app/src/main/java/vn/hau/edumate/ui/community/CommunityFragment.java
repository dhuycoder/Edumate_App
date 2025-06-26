package vn.hau.edumate.ui.community;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import vn.hau.edumate.R;
import vn.hau.edumate.data.enums.UserStatusCode;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.ui.auth.LoginActivity;
import vn.hau.edumate.ui.bottomsheet.TalkTypeBottomSheet;
import vn.hau.edumate.ui.adapter.MyViewPagerAdapter;
import vn.hau.edumate.ui.community.postcreate.PostCreateActivity;
import vn.hau.edumate.ui.community.profile.ProfileFragment;
import vn.hau.edumate.ui.home.HomeViewModel;
import vn.hau.edumate.ui.main.MainViewModel;

public class CommunityFragment extends Fragment {

    TabLayout tabLayout ;
    ViewPager2 viewPager2 ;
    MyViewPagerAdapter viewPagerAdapter ;

    ImageView btn_add_post;

    private BaseCommunityViewModel viewModel;
    private MainViewModel mainViewModel;
    private HomeViewModel homeViewModel;
    ImageButton btnProfile,btnSearch,btnNotify;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BaseCommunityViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_comunity);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_comunity, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);
        btn_add_post = view.findViewById(R.id.btn_create_post);
        viewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        btnProfile = view.findViewById(R.id.btn_profile);
        btnSearch = view.findViewById(R.id.btn_search);
        btnNotify = view.findViewById(R.id.btn_notify);
        homeViewModel.getIsLogout().observe(getViewLifecycleOwner(), isLogout -> {
            if(isLogout) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });
        Observer<DataResponse<UserResponse>> observer = new Observer<DataResponse<UserResponse>>() {
            @Override
            public void onChanged(DataResponse<UserResponse> userResponse) {
                if (userResponse.getData().getStatus() != UserStatusCode.NORMAL) {
                    if (userResponse.getData().getStatus() == UserStatusCode.LOCKED) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Thông báo")
                                .setMessage("Bạn đã bị khóa tài khoản.Vui lòng liên hệ quản trị viên để hỗ trợ")
                                .setPositiveButton("OK", (dialog, which) ->
                                        {
                                            homeViewModel.signOut(); // đăng xuất luôn khi ấn OK
                                            dialog.dismiss();
                                        })
                                .setCancelable(true)
                                .show();
                        return;
                    }

                    String expiredAtString = userResponse.getData().getExpiredAt();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
                    LocalDateTime expiredAt = LocalDateTime.parse(expiredAtString, formatter);

                    if (expiredAt.isAfter(LocalDateTime.now())) {
                        Duration remaining = Duration.between(LocalDateTime.now(), expiredAt);

                        long days = remaining.toDays();
                        long hours = remaining.toHours() % 24;
                        long minutes = remaining.toMinutes() % 60;

                        String remainingStr = String.format("%d ngày, %d giờ, %d phút", days, hours, minutes);

                        new AlertDialog.Builder(getContext())
                                .setTitle("Thông báo")
                                .setMessage(String.format(
                                        "Tài khoản đang bị cảnh báo (%s). " +
                                                "Vui lòng chờ đến khi hết hạn (còn lại: %s) hoặc liên hệ hỗ trợ.",
                                        userResponse.getData().getStatus().getDescription(), remainingStr))
                                .setPositiveButton("OK", (dialog, which) -> {
                                    homeViewModel.signOut(); // đăng xuất luôn khi ấn OK
                                    dialog.dismiss();

                                })
                                .setCancelable(true)
                                .show();
                    }
                } else {
                    TalkTypeBottomSheet bottomSheet = new TalkTypeBottomSheet(type -> {
                        Intent intent = new Intent(view.getContext(), PostCreateActivity.class);
                        intent.putExtra("TagNameEnumConstraint", type.name());
                        startActivity(intent);
                    });

                    bottomSheet.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), bottomSheet.getTag());
                }
            }
        };

        // Gắn observer
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), observer);


        btnProfile.setOnClickListener(v -> {
            mainViewModel.setCurrentFragment(new ProfileFragment());
        });
        btn_add_post.setOnClickListener(v ->{
            // Gọi API
            viewModel.getCurrentUser(view.getContext());

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
