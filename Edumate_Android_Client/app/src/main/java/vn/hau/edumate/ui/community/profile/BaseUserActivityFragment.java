package vn.hau.edumate.ui.community.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.hau.edumate.R;
import vn.hau.edumate.ui.adapter.CommentAdapter;
import vn.hau.edumate.ui.adapter.PostAdapter;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.ui.postdetail.PostDetailViewModel;
import vn.hau.edumate.util.FireBaseUtil;


public abstract class BaseUserActivityFragment extends Fragment {
    RecyclerView rcvMyPost;
    PostAdapter postAdapter;
    CommentAdapter commentAdapter;
    BaseCommunityViewModel baseCommunityViewModel;
    PostDetailViewModel postDetailViewModel;
    ProfileViewModel profileViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseCommunityViewModel = new ViewModelProvider(this).get(BaseCommunityViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        postDetailViewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        rcvMyPost = view.findViewById(R.id.rcv_my_post);
        postAdapter = new PostAdapter(view.getContext(), baseCommunityViewModel);
        commentAdapter = new CommentAdapter(view.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.VERTICAL,false);
        rcvMyPost.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(),RecyclerView.VERTICAL);
        rcvMyPost.addItemDecoration(itemDecoration);
            String userId = FireBaseUtil.getCurrentUserId();
            Log.d("MyPostsFragment", "userId: " + userId);
        loadData(userId);


        return view;
    }
    protected abstract void loadData(String userId);


}