package vn.hau.edumate.ui.history.semantic;

import android.annotation.SuppressLint;
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

import lombok.Getter;
import lombok.Setter;
import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.databinding.CardHistorySemanticSearchBinding;
import vn.hau.edumate.databinding.CardSemanticSearchResultBinding;

public class HistorySemanticSearchAdapter extends RecyclerView.Adapter<HistorySemanticSearchAdapter.HistorySemanticSearchViewHolder> {

    private static final String TAG = HistorySemanticSearchAdapter.class.getSimpleName();
    private List<SemanticSearchHistoryResponse> histories;
    private Context context;

    @Setter
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SemanticSearchHistoryResponse history);
        void onDeleteClick(SemanticSearchHistoryResponse history);
    }

    public HistorySemanticSearchAdapter(Context context) {
        this.context = context;
        this.histories = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSemanticSearchHistories(List<SemanticSearchHistoryResponse> histories) {
        this.histories = histories != null ? histories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistorySemanticSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardHistorySemanticSearchBinding binding = CardHistorySemanticSearchBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistorySemanticSearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorySemanticSearchViewHolder holder, int position) {
        SemanticSearchHistoryResponse history = histories.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    class HistorySemanticSearchViewHolder extends RecyclerView.ViewHolder {
        private CardHistorySemanticSearchBinding binding;
        public HistorySemanticSearchViewHolder(@NonNull CardHistorySemanticSearchBinding binding) {
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

        public void bind(SemanticSearchHistoryResponse history) {
            binding.tvDate.setText(history.getCreatedAt());
            Log.d("HistorySemanticSearchAdapter", "bind: " + history.getImageUrl());
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
