package vn.hau.edumate.ui.postdetail;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import okhttp3.MultipartBody;
import vn.hau.edumate.data.api.comment.CommentApi;
import vn.hau.edumate.data.api.image.ImageApi;
import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.model.request.CreateCommentRequest;
import vn.hau.edumate.data.model.request.ReportRequest;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.CommentRepository;
import vn.hau.edumate.data.repository.ImageRepository;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.ui.community.BaseCommunityViewModel;
import vn.hau.edumate.util.ResultCallback;

@Getter
@Setter
public class PostDetailViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final MutableLiveData<PostResponse> postLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CommentResponse>> commentListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> imageCommentId = new MutableLiveData<>();
    private final MutableLiveData<Integer> commentCount  = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> likeStatus  = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> _likeCommentStatus = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> _isCreatePostLoading = new MutableLiveData<>(false);
    private final LiveData<Boolean> isCreatePostLoading = _isCreatePostLoading;
    private final LiveData<Boolean> likeCommentStatus = _likeCommentStatus;

    public interface GetCommentByPostIdCallback {
        void onSuccess(List<CommentResponse> commentResponses);
        void onError(String error);
    }
    public interface SaveCallback {
        void onSuccess(CommentResponse commentResponse) ;
        void onError(String message);
    }


    public PostDetailViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository(ApiClient.createService(PostApi.class,application));
        commentRepository = new CommentRepository(ApiClient.createService(CommentApi.class,application));
        imageRepository = new ImageRepository(ApiClient.createService(ImageApi.class,application));
    }

    public void getPostById(Long postId) {
        postRepository.getPostById(postId, new PostRepository.GetPostByIdCallBack() {

            @Override
            public void onSuccess(PostResponse postResponse) throws IOException {
                postLiveData.postValue(postResponse);
            }

            @Override
            public void onError(String message) {
                Log.e("PostDetailViewModel", "Lỗi: " + message);
            }
        });
    }
    public void likePost(Long postId, Context context, BaseCommunityViewModel.LikeCallback likeCallback) {
        postRepository.likePost(postId, context,new PostRepository.LikePostCallBack() {
            @Override
            public void onSuccess(Integer likeCount) throws IOException {
                likeCallback.onSuccess(likeCount);
                likeStatus.setValue(Boolean.FALSE.equals(likeStatus.getValue()));
            }
            @Override
            public void onError(String message) {

                likeCallback.onError(message);
            }
        });
    }
    public void getCommentByPostId(Long postId , GetCommentByPostIdCallback callback){
        commentRepository.getCommentByPostId(postId, new CommentRepository.GetCommentByPostIdCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            @Override
            public void onSuccess(List<CommentResponse> commentResponses) throws IOException {
                commentListLiveData.setValue(commentResponses.stream()
                        .filter(commentResponse -> commentResponse.getParent() == null)
                        .toList());
                commentCount.setValue(commentResponses.size());
                callback.onSuccess(commentResponses);
            }

            @Override
            public void onError(String message) {
                Log.e("PostDetailViewModel", "Lỗi: " + message);
                callback.onError(message);
            }
        });
    }
    public void saveComment(CreateCommentRequest commentRequest,Long postId,Context context , SaveCallback callback) {
        _isCreatePostLoading.setValue(true);
        commentRepository.saveComment(commentRequest, postId, context ,new CommentRepository.SaveCallback() {
            @Override
            public void onSuccess(CommentResponse commentResponse) {
                callback.onSuccess(commentResponse);
                _isCreatePostLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                Log.e("PostDetailViewModel", "Lỗi: " + message);
                callback.onError(message);
                _isCreatePostLoading.setValue(false);
            }
        });
    }

    public void saveImage(List<MultipartBody.Part> multipartFiles,Context context) {
        _isCreatePostLoading.setValue(true);
        imageRepository.saveImage(multipartFiles, context ,new ResultCallback<DataResponse<List<ImageResponse>>>() {

            @Override
            public void onSuccess(DataResponse<List<ImageResponse>> result) {
                _isCreatePostLoading.setValue(false);
                imageCommentId.setValue(result.getData().get(0).getId());
            }

            @Override
            public void onError(Throwable throwable) {
                _isCreatePostLoading.setValue(false);
                Log.d("PostDetailViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    public void likeComment(Long commentId,Context context ,ResultCallback<Integer> callback) {
        _likeCommentStatus.setValue(Boolean.FALSE.equals(_likeCommentStatus.getValue()));
        commentRepository.likeComment(commentId,context,callback);
    }
    public void deletePost(Long postId, ResultCallback<DataResponse<PostResponse>> callback) {
        postRepository.deletePost(postId,callback);
    }
    public void reportPost(Long postId, ReportRequest reason, ResultCallback<DataResponse<Void>> callback) {
        postRepository.reportPost(postId,reason,callback);
    }
    public void hidePost(Long postId, ResultCallback<DataResponse<Void>> callback){
        postRepository.hidePost(postId,callback);
    }
}
