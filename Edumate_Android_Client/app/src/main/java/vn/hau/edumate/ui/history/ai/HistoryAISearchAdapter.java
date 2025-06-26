package vn.hau.edumate.ui.history.ai;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.response.AISearchHistoryResponse;
import vn.hau.edumate.databinding.CardHistoryAiSearchBinding;

public class HistoryAISearchAdapter extends RecyclerView.Adapter<HistoryAISearchAdapter.HistoryAISearchViewHolder> {
    private List<AISearchHistoryResponse> histories;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AISearchHistoryResponse history);
        void onDeleteClick(AISearchHistoryResponse history);
    }

    public HistoryAISearchAdapter(Context context) {
        this.context = context;
        this.histories = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setHistories(List<AISearchHistoryResponse> histories) {
        this.histories = histories != null ? histories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryAISearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardHistoryAiSearchBinding binding = CardHistoryAiSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryAISearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAISearchViewHolder holder, int position) {
        AISearchHistoryResponse history = histories.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    class HistoryAISearchViewHolder extends RecyclerView.ViewHolder {
        private CardHistoryAiSearchBinding binding;

        public HistoryAISearchViewHolder(@NonNull CardHistoryAiSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(histories.get(getAdapterPosition()));
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onDeleteClick(histories.get(getAdapterPosition()));
                }
            });
        }

        public void bind(AISearchHistoryResponse history) {
            binding.tvDate.setText(history.getCreatedAt());
            Log.d("HistoryAISearchAdapter", "bind: " + history.getImageUrl());
            if (history.getImageUrl() != null && !history.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(SystemConstant.BASE_URL + history.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_img_error)
                        .into(binding.img);
            } else {
                binding.img.setImageResource(R.drawable.ic_placeholder);
            }
        }
    }
}