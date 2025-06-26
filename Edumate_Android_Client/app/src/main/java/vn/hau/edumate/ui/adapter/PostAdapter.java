package vn.hau.edumate.ui.adapter;


import static vn.hau.edumate.util.TimeUtils.getTimeAgo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.ui.postdetail.PostDetailActivity;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context mContext;
    private final BaseCommunityViewModel viewModel;

    private List<PostResponse> mListPost = new ArrayList<>();


    public PostAdapter(Context mContext , BaseCommunityViewModel viewModel) {
        this.mContext = mContext;
        this.viewModel = viewModel;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<PostResponse> posts){
        this.mListPost = posts;
        notifyDataSetChanged();

    }
    public void addPosts(List<PostResponse> newPosts) {
        int start = mListPost.size();
        mListPost.addAll(newPosts);
        notifyItemRangeInserted(start, newPosts.size());
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new PostViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostResponse post = mListPost.get(position);

        Optional.ofNullable(post).ifPresentOrElse(p -> {
            // Text
            holder.tvName.setText(p.getAuthor().getUsername());
            holder.tvPostCreate.setText(getTimeAgo(p.getCreatedAt()));
            holder.tvTag.setText(p.getTag().getName());
            holder.tvTitle.setText(p.getTitle());
            holder.tvContent.setText(p.getContent());
            holder.tvCommentCount.setText(String.valueOf(p.getCommentCount()));
            holder.tvLikeCount.setText(String.valueOf(p.getLikeCount()));

            // Ảnh đại diện
            Glide.with(mContext)
                    .load(p.getAuthor().getAvatarUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .into(holder.imgUser);

            // Reset trạng thái mặc định trước
            holder.imgPost.setImageDrawable(null);
            holder.imgPost.setVisibility(View.GONE);
            holder.tvImageCount.setVisibility(View.GONE);

            // Xử lý ảnh
            List<ImageResponse> images = p.getImages();
            if (images != null && !images.isEmpty()) {
                holder.imgPost.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(SystemConstant.BASE_URL + "/image/" + images.get(0).getId())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(300, 200)
                        .into(holder.imgPost);

                if (images.size() > 1) {
                    holder.tvImageCount.setVisibility(View.VISIBLE);
                    holder.tvImageCount.setText(String.valueOf(images.size() - 1));
                }
            }

            // Click: sang chi tiết
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
                intent.putExtra("post_id", p.getId());
                v.getContext().startActivity(intent);
            });

            // Like

            holder.btnLike.setSelected(p.isLiked());
            AtomicBoolean isLiked = new AtomicBoolean(p.isLiked());
            holder.btnLike.setOnClickListener(view ->
                    viewModel.likePost(p.getId(), mContext ,new BaseCommunityViewModel.LikeCallback() {

                        @Override
                        public void onSuccess(int likeCount) {
                            holder.tvLikeCount.setText(String.valueOf(likeCount));
                            isLiked.set(!isLiked.get());
                            holder.btnLike.setSelected(isLiked.get());
                        }


                        @Override
                        public void onError(String error) {
                           Log.d("HomeViewModel:",error);
                        }
                    })
            );

        }, null);
    }


    @Override
    public int getItemCount() {
        if(mListPost == null){
            return 0;
        }
        if(!mListPost.isEmpty()){
            return mListPost.size();
        }
        return 0;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imgUser;
        private final TextView tvName;
        private final TextView tvPostCreate;
        private final TextView tvTag;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final ImageView imgPost;
        private final TextView tvImageCount;
        private final Button btnLike;

        private final TextView tvCommentCount;
        private final TextView tvLikeCount;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.post_image);
            imgUser = itemView.findViewById(R.id.img_user_post);
            tvName = itemView.findViewById(R.id.name_user_post);
            tvPostCreate = itemView.findViewById(R.id.post_create_date);
            tvTag = itemView.findViewById(R.id.tag_post);
            tvTitle = itemView.findViewById(R.id.title_post);
            tvContent = itemView.findViewById(R.id.post_content);
            btnLike = itemView.findViewById(R.id.btn_like);
            tvImageCount = itemView.findViewById(R.id.image_count_overlay);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);

        }
    }


}


