package vn.hau.edumate.ui.camera.semantic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.databinding.CardSemanticSearchResultBinding;

public class SemanticSearchResultAdapter extends RecyclerView.Adapter<SemanticSearchResultAdapter.SemanticSearchViewHolder> {
    private static final String TAG = SemanticSearchResultAdapter.class.getSimpleName();
    private Context context;
    private List<SemanticSearchResponse> semanticSearchResponses;

    @Setter
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(SemanticSearchResponse response);
    }

    public SemanticSearchResultAdapter(Context context) {
        this.context = context;
        this.semanticSearchResponses = new ArrayList<>();
    }

    public void setSemanticSearchResponses(List<SemanticSearchResponse> semanticSearchResponses) {
        this.semanticSearchResponses = semanticSearchResponses != null ? semanticSearchResponses : new ArrayList<>();
        notifyDataSetChanged();
    }

    class SemanticSearchViewHolder extends RecyclerView.ViewHolder {
        private CardSemanticSearchResultBinding binding;
        public SemanticSearchViewHolder(@NonNull CardSemanticSearchResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(semanticSearchResponses.get(getAdapterPosition()));
                }
            });
        }

        public void bind(SemanticSearchResponse response) {
            if(response.getImageUrl() != null && !response.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(SystemConstant.BASE_URL + response.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_img_error)
                        .centerCrop()
                        .into(binding.img);
            } else {
                binding.img.setImageResource(R.drawable.ic_placeholder);
            }
        }
    }

    @NonNull
    @Override
    public SemanticSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardSemanticSearchResultBinding binding = CardSemanticSearchResultBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SemanticSearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SemanticSearchViewHolder holder, int position) {
        SemanticSearchResponse response = semanticSearchResponses.get(position);
        holder.bind(response);
    }

    @Override
    public int getItemCount() {
        return semanticSearchResponses.size();
    }
}
