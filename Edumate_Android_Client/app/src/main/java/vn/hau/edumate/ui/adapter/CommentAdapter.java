package vn.hau.edumate.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import vn.hau.edumate.R;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.ui.comment.CommentActivity;
import vn.hau.edumate.ui.postdetail.PostDetailActivity;
import vn.hau.edumate.ui.postdetail.PostDetailViewModel;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final Context mContext;

    private List<CommentResponse> mListComment = new ArrayList<>();

    public CommentAdapter(Context mContext) {
        this.mContext = mContext;

    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<CommentResponse> comments){
        this.mListComment = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_comment_item,parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResponse comment = mListComment.get(position);
        Optional.ofNullable(comment).ifPresentOrElse((c) -> {
            holder.tvContent.setText(c.getContent());
            holder.tvCreateDate.setText(c.getCreatedAt());
            holder.itemView.setOnClickListener(v -> {
                Intent intent;
                if(c.getParent() == null ){
                    intent = new Intent(v.getContext(), PostDetailActivity.class);
                    intent.putExtra("post_id", c.getPost().getId());
                }
                else{
                    intent = new Intent(v.getContext(), CommentActivity.class);
                    intent.putExtra("comment_id", c.getParent().getId());
                }
                v.getContext().startActivity(intent);
            });
        },null);

    }

    @Override
    public int getItemCount() {
        if(!mListComment.isEmpty()){
            return mListComment.size();
        }
        return 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvContent;
        private final TextView tvCreateDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvCreateDate = itemView.findViewById(R.id.tv_create_date);
        }

    }
}
