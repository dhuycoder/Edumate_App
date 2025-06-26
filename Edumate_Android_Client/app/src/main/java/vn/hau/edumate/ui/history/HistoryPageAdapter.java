package vn.hau.edumate.ui.history;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.hau.edumate.ui.history.ai.HistoryAISearchFragment;
import vn.hau.edumate.ui.history.semantic.HistorySemanticSearchFragment;


public class HistoryPageAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 2;

    public HistoryPageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> new HistoryAISearchFragment();
            case 1 -> new HistorySemanticSearchFragment();
            default -> new HistoryAISearchFragment();
        };
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
