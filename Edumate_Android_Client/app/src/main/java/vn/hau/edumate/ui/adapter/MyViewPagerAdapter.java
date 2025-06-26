package vn.hau.edumate.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.hau.edumate.ui.community.CommunityFragment;
import vn.hau.edumate.ui.community.homeworksupport.HomeWorkSupportFragment;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.ui.community.homeworksupport.HomeWorkSupportViewModel;
import vn.hau.edumate.ui.community.sharingknowledge.SharingKnowLedgeFragment;
import vn.hau.edumate.ui.community.sharingknowledge.SharingKnowLedgeViewModel;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull CommunityFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new SharingKnowLedgeFragment(SharingKnowLedgeViewModel.class);
            case 1 -> new HomeWorkSupportFragment(HomeWorkSupportViewModel.class);
            default -> new SharingKnowLedgeFragment(SharingKnowLedgeViewModel.class);
        };
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
