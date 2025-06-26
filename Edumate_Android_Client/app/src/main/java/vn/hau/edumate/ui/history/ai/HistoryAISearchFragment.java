package vn.hau.edumate.ui.history.ai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import vn.hau.edumate.data.model.response.AISearchHistoryResponse;
import vn.hau.edumate.databinding.FragmentAiHistoryBinding;

public class HistoryAISearchFragment extends Fragment {
    private final static String TAG = "HistoryAISearchFragment";

    private FragmentAiHistoryBinding binding;
    private HistoryAISearchViewModel viewModel;
    private HistoryAISearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerViewAndViewModel();
    }

    @SuppressLint("SetTextI18n")
    private void setupRecyclerViewAndViewModel() {
        adapter = new HistoryAISearchAdapter(requireContext());
        binding.root.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.root.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(HistoryAISearchViewModel.class);

        viewModel.getHistoriesLiveData().observe(getViewLifecycleOwner(), histories -> {
            if (histories != null) {
                adapter.setHistories(histories);
            }
        });

        viewModel.getSumHistory().observe(getViewLifecycleOwner(), sum -> {
            binding.sumResult.setText("🤖 Tổng số kết quả: " + sum);

            if (sum > 0) {
                binding.btnDeleteAll.setEnabled(true);
                binding.btnDeleteAll.setAlpha(1.0f);
            } else {
                binding.btnDeleteAll.setEnabled(false);
                binding.btnDeleteAll.setAlpha(0.5f);
            }
        });

        binding.btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa tất cả lịch sử")
                    .setMessage("Bạn có chắc chắn muốn xóa tất cả lịch sử tìm kiếm AI?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        viewModel.deleteAllHistories(getContext());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        adapter.setOnItemClickListener(new HistoryAISearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AISearchHistoryResponse history) {
                showHistoryDetail(history);
            }

            @Override
            public void onDeleteClick(AISearchHistoryResponse history) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa lịch sử")
                        .setMessage("Bạn có chắc chắn muốn xóa lịch sử này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            viewModel.deleteHistory(getContext(),history.getId());
                            dialog.dismiss();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    private void loadData() {
        Log.d(TAG, "Load history data");
        viewModel.fetchHistories(null);
    }

    private void showHistoryDetail(AISearchHistoryResponse history) {
        Intent intent = new Intent(requireContext(), HistoryAISearchDetailActivity.class);
        intent.putExtra("history", (Parcelable) history);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
