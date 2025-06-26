package vn.hau.edumate.ui.history.semantic;

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

import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.databinding.FragmentSemanticHistoryBinding;
import vn.hau.edumate.ui.camera.semantic.SemanticLoadingActivity;
import vn.hau.edumate.ui.postdetail.PostDetailActivity;


public class HistorySemanticSearchFragment extends Fragment {
    private static final String TAG = HistorySemanticSearchFragment.class.getSimpleName();
    private FragmentSemanticHistoryBinding binding;
    private HistorySemanticSearchViewModel viewModel;
    private HistorySemanticSearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSemanticHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        viewModel = new ViewModelProvider(this).get(HistorySemanticSearchViewModel.class);
        observerData();

        binding.btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xóa tất cả lịch sử")
                    .setMessage("Bạn có chắc chắn muốn xóa tất cả lịch sử?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        viewModel.deleteAllHistories();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();

        });
    }

    private void setupRecyclerView() {
        adapter = new HistorySemanticSearchAdapter(requireContext());
        binding.root.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.root.setAdapter(adapter);
        adapter.setOnItemClickListener(new HistorySemanticSearchAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(SemanticSearchHistoryResponse history) {
                showHistoryDetail(history);
            }

            @Override
            public void onDeleteClick(SemanticSearchHistoryResponse history) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xóa lịch sử")
                        .setMessage("Bạn có chắc chắn muốn xóa lịch sử này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            viewModel.deleteHistory(history.getId());
                            dialog.dismiss();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void observerData() {
        viewModel.getHistoriesLiveData().observe(getViewLifecycleOwner(), histories -> {
            if (histories != null) {
                adapter.setSemanticSearchHistories(histories);
            }
        });

        viewModel.getSumHistory().observe(getViewLifecycleOwner(), sum -> {
            binding.sumResult.setText("📒 Tổng số kết quả: " + sum);

            if (sum > 0) {
                binding.btnDeleteAll.setEnabled(true);
                binding.btnDeleteAll.setAlpha(1.0f);
            } else {
                binding.btnDeleteAll.setEnabled(false);
                binding.btnDeleteAll.setAlpha(0.5f);
            }
        });
    }

    private void showHistoryDetail(SemanticSearchHistoryResponse semanticSearchHistoryResponse) {
        Log.d(TAG, "showHistoryDetail: " + semanticSearchHistoryResponse.getId());
        Intent intent = new Intent(requireContext(), SemanticLoadingActivity.class);
        intent.putExtra("response", (Parcelable) semanticSearchHistoryResponse);
        startActivity(intent);
    }

    private void loadData() {
        viewModel.fetchHistories();
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
