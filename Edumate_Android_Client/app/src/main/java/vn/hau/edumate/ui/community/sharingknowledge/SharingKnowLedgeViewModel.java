package vn.hau.edumate.ui.community.sharingknowledge;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedHashSet;
import java.util.List;

import lombok.Getter;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.util.ResultCallback;

@Getter
public class SharingKnowLedgeViewModel extends BaseCommunityViewModel {
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final LiveData<Boolean> isLoading = _isLoading;
    private final MutableLiveData<Boolean> _isNoPost = new MutableLiveData<>(false);
    private final LiveData<Boolean> isNoPost = _isNoPost;

    private boolean hasNextPage = true;
    private int currentPage = 1;
    public SharingKnowLedgeViewModel(@NonNull Application application) {
        super(application);
    }
    public void resetPaging() {
        currentPage = 1;
        hasNextPage = true;
        _isLoading.setValue(false);

    }
    public void loadingPage(TagType tagType, Context context, ResultCallback<LinkedHashSet<PostResponse>> resultCallback){
        if (Boolean.TRUE.equals(_isLoading.getValue()) || !hasNextPage) return;
        _isLoading.setValue(true);
        postRepository.getPostByTagType(tagType,currentPage, SystemConstant.LIMIT,context,new PostRepository.GetPostCallBack() {
            @Override
            public void onSuccess(LinkedHashSet<PostResponse> postResponses)  {
                _isLoading.setValue(false);
                LinkedHashSet<PostResponse> newPosts = postResponses;
                LinkedHashSet<PostResponse> currentPosts = postListLiveDataByTagType.getValue();

                if (currentPosts == null) {
                    currentPosts = new LinkedHashSet<>();
                }

                currentPosts.addAll(newPosts);
                postListLiveDataByTagType.setValue(currentPosts);


                currentPage++;
                hasNextPage = !newPosts.isEmpty();
                resultCallback.onSuccess(currentPosts);

            }

            @Override
            public void onError(Throwable message) {
                _isLoading.setValue(false);
                Log.e("HomeWorkSupportViewModel", "Lỗi: " + message);
                resultCallback.onError(message);
            }
        });
    }
}
