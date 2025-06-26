package vn.hau.edumate.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import vn.hau.edumate.R;
import vn.hau.edumate.data.model.response.ChatMessage;

public class ChatAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder >  {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int TYPE_LOADING = 3;
    private int lastAnimatedPosition = -1;

    private final List<ChatMessage> messages;
    private final Context context;
    public ChatAdapter(List<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_search_ai, parent, false);
            return new LoadingViewHolder(view);
        }
        int layout = viewType == VIEW_TYPE_USER ? R.layout.item_user_message : R.layout.item_bot_message;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadingMessageBot);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,  int position) {
        int pos = holder.getAdapterPosition();// dam bao khong thay doi vi tri item
        // Xử lý riêng cho từng loại ViewHolder
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).bind(messages.get(position));

            // Chỉ thêm animation cho tin nhắn mới
            if (position > lastAnimatedPosition) {
                Animation animation = AnimationUtils.loadAnimation(context,
                        messages.get(position).isUser() ? R.anim.slide_in_right : R.anim.slide_in_left);
                holder.itemView.startAnimation(animation);
                lastAnimatedPosition = pos;
            }
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.playAnimation();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.isLoading()) {
            return TYPE_LOADING;
        }
        return message.isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }

        public void bind(ChatMessage message) {
            messageText.setText(message.getContent());
        }
    }
}
