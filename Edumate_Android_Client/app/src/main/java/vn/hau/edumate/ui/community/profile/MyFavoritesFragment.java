package vn.hau.edumate.ui.community.profile;

import java.util.ArrayList;
import java.util.List;

import vn.hau.edumate.data.model.response.PostResponse;

public class MyFavoritesFragment extends BaseUserActivityFragment{
    @Override
    protected void loadData(String userId) {
        profileViewModel.getPostByUserLikeLiveData().observe(getViewLifecycleOwner(), dataResponse -> {
            if (dataResponse != null && dataResponse.getData() != null) {
                List<PostResponse> postList = new ArrayList<>(dataResponse.getData());
                postAdapter.setData(postList);
                rcvMyPost.setAdapter(postAdapter);
            }
        });
        profileViewModel.getPostByCurrentUserLike(getContext());
    }
}
