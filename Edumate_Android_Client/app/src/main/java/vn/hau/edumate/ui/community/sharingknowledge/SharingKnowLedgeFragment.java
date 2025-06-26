package vn.hau.edumate.ui.community.sharingknowledge;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import vn.hau.edumate.R;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.model.response.TagResponse;
import vn.hau.edumate.ui.community.BaseCommunityFragment;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.util.ResultCallback;


public class SharingKnowLedgeFragment extends BaseCommunityFragment<SharingKnowLedgeViewModel> {
    protected boolean isScrollListenerAttached = false;
    protected RecyclerView.OnScrollListener scrollListener;
    private Button selectedButton = null;

    public SharingKnowLedgeFragment(Class<SharingKnowLedgeViewModel> viewModel) {
        super(viewModel);
    }

    @Override
    protected void setLoadingPage() {

        viewModel.getPostListLiveDataByTagType().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null) {
                // Chưa có dữ liệu gì -> có thể show progress bar
                layoutNoPost.setVisibility(VISIBLE);
                return;
            }
            if(posts.isEmpty()){
                layoutNoPost.setVisibility(VISIBLE);
            }else{
                layoutNoPost.setVisibility(GONE);
                List<PostResponse> postList = new ArrayList<>(posts);
                postAdapter.setData(postList);
            }
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingPageLayout.setVisibility(isLoading ? VISIBLE : View.GONE);
        });
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm != null && lm.findLastCompletelyVisibleItemPosition() == postAdapter.getItemCount() - 1) {
                    viewModel.loadingPage(TagType.SHARING_KNOWLEDGE, null, new ResultCallback<LinkedHashSet<PostResponse>>() {
                        @Override
                        public void onSuccess(LinkedHashSet<PostResponse> result) {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });
                }
            }
        };
        if (!isScrollListenerAttached) {
            rcvPost.addOnScrollListener(scrollListener);
            isScrollListenerAttached = true;
        }
        viewModel.loadingPage(TagType.SHARING_KNOWLEDGE, null, new ResultCallback<LinkedHashSet<PostResponse>>() {
            @Override
            public void onSuccess(LinkedHashSet<PostResponse> result) {

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }


    @Override
    protected void loadTagButton() {

        viewModel.getTagListLiveData().observe(getViewLifecycleOwner(), tagResponses -> {
            View customButtonLayout = getLayoutInflater().inflate(R.layout.button_tag, layoutBtnTag, false);
            Button btn = customButtonLayout.findViewById(R.id.btn_tag);

            btn.setText("All");
            Button finalBtn1 = btn;
            btn.setOnClickListener(v -> {
                // 👉 Bỏ highlight button cũ nếu có
                if (selectedButton != null) {
                    selectedButton.setBackgroundResource(R.drawable.rounded_border_button); // hoặc set màu mặc định
                    selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                }
                // 👉 Highlight button hiện tại
                finalBtn1.setBackgroundResource(R.drawable.bg_button_selected); // hoặc set màu khi được chọn
                finalBtn1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                selectedButton = finalBtn1; // Lưu lại button đang được chọn
                viewModel.resetPaging();
                viewModel.loadingPage(TagType.SHARING_KNOWLEDGE,null,new ResultCallback<LinkedHashSet<PostResponse>>() {
                    @Override
                    public void onSuccess(LinkedHashSet<PostResponse> result) {
                        rcvPost.setVisibility(VISIBLE);
                        layoutNoPost.setVisibility(GONE);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });

                if (!isScrollListenerAttached) {
                    rcvPost.addOnScrollListener(scrollListener);
                    isScrollListenerAttached = true;
                }
            });

            layoutBtnTag.addView(customButtonLayout);
            if (tagResponses != null) {
                for (TagResponse tagResponse : tagResponses) {
                    customButtonLayout = getLayoutInflater().inflate(R.layout.button_tag, layoutBtnTag, false);

                    btn = customButtonLayout.findViewById(R.id.btn_tag);
                    btn.setText(tagResponse.getName());

                    Button finalBtn = btn;
                    btn.setOnClickListener(v -> {
                        // 👉 Bỏ highlight button cũ nếu có
                        if (selectedButton != null) {
                            selectedButton.setBackgroundResource(R.drawable.rounded_border_button); // hoặc set màu mặc định
                            selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                        }

                        // 👉 Highlight button hiện tại
                        finalBtn.setBackgroundResource(R.drawable.bg_button_selected); // hoặc set màu khi được chọn
                        finalBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                        selectedButton = finalBtn; // Lưu lại button đang được chọn

                        // Remove old scroll listener
                        if (scrollListener != null) {
                            rcvPost.removeOnScrollListener(scrollListener);
                            isScrollListenerAttached = false;
                        }

                        viewModel.getPostByTag(tagResponse.getId(), null, new BaseCommunityViewModel.getPostByTagCallBack() {
                            @Override
                            public void onSuccess(LinkedHashSet<PostResponse> postResponses) {
                                viewModel.getPostListLiveData().postValue(postResponses);
                                rcvPost.setVisibility(VISIBLE);
                                layoutNoPost.setVisibility(GONE);
                            }

                            @Override
                            public void onError(String message) {
                                layoutNoPost.setVisibility(VISIBLE);
                                rcvPost.setVisibility(GONE);
                            }
                        });
                    });

                    layoutBtnTag.addView(customButtonLayout);
                }
            }
        });
        // Gọi API
        viewModel.getTagByType(TagType.SHARING_KNOWLEDGE,getContext());
    }

    @Override
    protected void loadPostByTagType() {

        viewModel.getPostListLiveData().observe(getViewLifecycleOwner(),postResponses -> {
            if(!postResponses.isEmpty()){
                List<PostResponse> postList = new ArrayList<>(postResponses);
                postAdapter.setData(postList);

            }
        });
//
//        viewModel.getPostByTagType(TagType.SHARING_KNOWLEDGE,
//                getContext(),new ResultCallback<LinkedHashSet<PostResponse>>(){
//
//            @Override
//            public void onSuccess(LinkedHashSet<PostResponse> result) {
//
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//
//            }
//        });
    }
}