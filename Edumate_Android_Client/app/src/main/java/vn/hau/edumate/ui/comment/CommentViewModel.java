package vn.hau.edumate.ui.comment;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import okhttp3.MultipartBody;
import retrofit2.Call;
import vn.hau.edumate.data.api.comment.CommentApi;
import vn.hau.edumate.data.api.image.ImageApi;
import vn.hau.edumate.data.model.request.CreateCommentRequest;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.CommentRepository;
import vn.hau.edumate.data.repository.ImageRepository;
import vn.hau.edumate.ui.postdetail.PostDetailViewModel;
import vn.hau.edumate.util.ResultCallback;

@Getter
public class CommentViewModel extends AndroidViewModel {
    private final MutableLiveData<CommentResponse> commentLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CommentResponse>> repliesListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> imageRepliesId = new MutableLiveData<>();
    private final CommentRepository commentRepository;
    private  final ImageRepository imageRepository;

    public interface SaveCallback {
        void onSuccess(CommentResponse commentResponse) ;
        void onError(String message);
    }

    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentRepository = new CommentRepository(ApiClient.createService(CommentApi.class,application));
        imageRepository = new ImageRepository(ApiClient.createService(ImageApi.class,application));
    }

    public void getCommentById(Long commentId){
        commentRepository.getCommentById(commentId,new ResultCallback<CommentResponse>() {
            @Override
            public void onSuccess(CommentResponse result) {
                commentLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("PostDetailViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    public void getRepliesByParentId(Long parentId) {
        commentRepository.getRepliesByParentId(parentId, new ResultCallback<List<CommentResponse>>() {

            @Override
            public void onSuccess(List<CommentResponse> result) {
                repliesListLiveData.setValue(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("PostDetailViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
    public void saveReplies(CreateCommentRequest commentRequest, Long parentId ,Context context ,SaveCallback callback){
        commentRepository.saveReplies(commentRequest,parentId, context,new CommentRepository.SaveCallback(){
            @Override
            public void onSuccess(CommentResponse commentResponse) {
                callback.onSuccess(commentResponse);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        } );
    }
    public void likeComment(Long commentId,Context context ,ResultCallback<Integer> callback) {
        commentRepository.likeComment(commentId,context,callback);
    }
    public void saveImage(List<MultipartBody.Part> multipartFiles, Context context) {
        imageRepository.saveImage(multipartFiles, context, new ResultCallback<DataResponse<List<ImageResponse>>>() {

            @Override
            public void onSuccess(DataResponse<List<ImageResponse>> result) {
                imageRepliesId.setValue(result.getData().get(0).getId());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("PostDetailViewModel", "Lỗi: " + throwable.getMessage());
            }
        });
    }
}
