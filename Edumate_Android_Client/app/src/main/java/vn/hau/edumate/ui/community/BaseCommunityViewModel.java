package vn.hau.edumate.ui.community;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

import lombok.Getter;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.user.UserApi;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.data.network.ApiClient;

import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.api.tag.TagApi;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.model.response.TagResponse;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.data.repository.TagRepository;
import vn.hau.edumate.data.repository.UserRepository;
import vn.hau.edumate.util.ResultCallback;

@Getter
public class BaseCommunityViewModel extends AndroidViewModel {
    protected final TagRepository tagRepository;
    protected  PostRepository postRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<DataResponse<UserResponse>> userLiveData = new MutableLiveData<>();


    private final MutableLiveData<List<TagResponse>> tagListLiveData = new MutableLiveData<>();

    private final MutableLiveData<LinkedHashSet<PostResponse>> postListLiveData = new MutableLiveData<>(new LinkedHashSet<>());
    protected final MutableLiveData<LinkedHashSet<PostResponse>> postListLiveDataByTagType = new MutableLiveData<>(null);



    public interface LikeCallback {
        void onSuccess(int likeCount);
        void onError(String error);
    }

    public interface getPostByTagCallBack{
        void onSuccess(LinkedHashSet<PostResponse> postResponses);
        void onError(String message);

    }
    public BaseCommunityViewModel(@NonNull Application application) {
        super(application);
        tagRepository = new TagRepository(ApiClient.createService(TagApi.class,application));

        postRepository = new PostRepository(ApiClient.createService(PostApi.class,application));

        userRepository = new UserRepository(ApiClient.createService(UserApi.class,application));
    }

    public void getPostByTagType(TagType tagType, Context context,ResultCallback<LinkedHashSet<PostResponse>> callback) {
        postRepository.getPostByTagType(tagType, SystemConstant.PAGE,SystemConstant.LIMIT,context,new PostRepository.GetPostCallBack() {
            @Override
            public void onSuccess(LinkedHashSet<PostResponse> postResponses)  {
                postListLiveData.postValue(postResponses);
                callback.onSuccess(postResponses);
            }

            @Override
            public void onError(Throwable message) {
                Log.e("HomeWorkSupportViewModel", "Lỗi: " + message);
                callback.onError(message);
            }
        });
    }
    public void getPostByTag(Long tagId,Context context, getPostByTagCallBack getPostByTagCallBack){
        postRepository.getPostByTag(tagId,context ,new PostRepository.GetPostByTagCallBack() {
            @Override
            public void onSuccess(LinkedHashSet<PostResponse> postResponses) {
               getPostByTagCallBack.onSuccess(postResponses);
            }
            @Override
            public void onError(String message) {
                getPostByTagCallBack.onError(message);
            }
        });
    }
    public void getTagByType(TagType tagType,Context context) {

        tagRepository.getTagsByTagType(tagType,context ,new TagRepository.getTagByType() {
            @Override
            public void onSuccess(List<TagResponse> tagResponses) {
                tagListLiveData.postValue(tagResponses);
            }
            @Override
            public void onError(String message) {
                Log.e("HomeWorkSupportViewModel", "Lỗi: " + message);
            }
        });

    }

    public void likePost(Long postId, Context context, LikeCallback likeCallback) {
        postRepository.likePost(postId,context ,new PostRepository.LikePostCallBack() {
            @Override
            public void onSuccess(Integer likeCount) throws IOException {
                    likeCallback.onSuccess(likeCount);
            }
            @Override
            public void onError(String message) {

                likeCallback.onError(message);
            }
        });
    }
    public void getCurrentUser(Context context){
        userRepository.getCurrentUser(context,new ResultCallback<DataResponse<UserResponse>>() {
            @Override
            public void onSuccess(DataResponse<UserResponse> result) {
                userLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("BaseCommunityViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }


}
