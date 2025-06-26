package vn.hau.edumate.ui.community.profile;

public class MyCommentFragment extends BaseUserActivityFragment{
    @Override
    protected void loadData(String userId) {
        profileViewModel.getCommentLiveData().observe(getViewLifecycleOwner(), commentResponses -> {
            commentAdapter.setData(commentResponses);
            rcvMyPost.setAdapter(commentAdapter);
        });
        profileViewModel.getCommentsAndRepliesByCurrentUser(getContext());
    }
}
