package vn.hau.edumate.ui.community.profile;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.LinkedHashSet;
import java.util.List;

import lombok.Getter;
import vn.hau.edumate.data.api.comment.CommentApi;
import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.api.user.UserApi;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.CommentRepository;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.data.repository.UserRepository;
import vn.hau.edumate.util.ResultCallback;
@Getter
public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MutableLiveData<DataResponse<UserResponse>> _userLiveData = new MutableLiveData<>();
    private final LiveData<DataResponse<UserResponse>> userLiveData = _userLiveData;
    private final MutableLiveData<DataResponse<LinkedHashSet<PostResponse>>> _postLiveData = new MutableLiveData<>();
    private final LiveData<DataResponse<LinkedHashSet<PostResponse>>> postLiveData = _postLiveData;
    private final MutableLiveData<DataResponse<LinkedHashSet<PostResponse>>> _postByUserLikeLiveData = new MutableLiveData<>();
    private final LiveData<DataResponse<LinkedHashSet<PostResponse>>> postByUserLikeLiveData = _postByUserLikeLiveData;
    private final MutableLiveData<List<CommentResponse>> _commentLiveData = new MutableLiveData<>();
    private final LiveData<List<CommentResponse>> commentLiveData = _commentLiveData;
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(ApiClient.createService(UserApi.class,application));
        postRepository = new PostRepository(ApiClient.createService(PostApi.class,application));
        commentRepository = new CommentRepository(ApiClient.createService(CommentApi.class,application));
    }
    public void getCurrentUser(Context context){
        userRepository.getCurrentUser(context,new ResultCallback<DataResponse<UserResponse>>() {
            @Override
            public void onSuccess(DataResponse<UserResponse> result) {
                _userLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ProfileViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    public void getPostByUserId(String userId, Context context) {
        postRepository.getPostByUserId(userId, context, new ResultCallback<DataResponse<LinkedHashSet<PostResponse>>>() {
            @Override
            public void onSuccess(DataResponse<LinkedHashSet<PostResponse>> result) {
                _postLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ProfileViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    public void getPostByCurrentUserLike(Context context) {
        postRepository.getPostByCurrentUserLike(context, new ResultCallback<DataResponse<LinkedHashSet<PostResponse>>>() {

            @Override
            public void onSuccess(DataResponse<LinkedHashSet<PostResponse>> result) {
                _postByUserLikeLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ProfileViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    public void getCommentsAndRepliesByCurrentUser(Context context) {
        commentRepository.getCommentsAndRepliesByCurrentUser(context, new ResultCallback<List<CommentResponse>>() {

            @Override
            public void onSuccess(List<CommentResponse> result) {
                _commentLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("ProfileViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
}
