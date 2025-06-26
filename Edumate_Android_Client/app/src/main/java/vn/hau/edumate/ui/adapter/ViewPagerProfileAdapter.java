package vn.hau.edumate.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.hau.edumate.ui.community.profile.MyCommentFragment;
import vn.hau.edumate.ui.community.profile.MyFavoritesFragment;
import vn.hau.edumate.ui.community.profile.MyPostFragment;
import vn.hau.edumate.ui.community.profile.ProfileFragment;


public class ViewPagerProfileAdapter extends FragmentStateAdapter {
    public ViewPagerProfileAdapter(@NonNull ProfileFragment fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new MyPostFragment();
            case 1 -> new MyCommentFragment();
            case 2 -> new MyFavoritesFragment();
            default -> new MyPostFragment();
        };
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
