package vn.hau.edumate.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import vn.hau.edumate.R;
import vn.hau.edumate.databinding.BaseFragmentHistoryBinding;

public class BaseHistoryFragment extends Fragment {

    private BaseFragmentHistoryBinding binding;
    private HistoryPageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BaseFragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager();
        setupTabs();
    }

    private void setupViewPager() {
        adapter = new HistoryPageAdapter(this);
        binding.viewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Hỏi A.I");
                            break;
                        case 1:
                            tab.setText("Tìm kiếm");
                            break;
                    }
                }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
