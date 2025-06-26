package vn.hau.edumate.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.comment.CommentApi;
import vn.hau.edumate.data.model.request.CreateCommentRequest;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.util.ResultCallback;

public class CommentRepository {
    private final CommentApi commentApi;

    public CommentRepository(CommentApi commentApi) {
        this.commentApi = commentApi;
    }
    public interface GetCommentByPostIdCallBack{
        void onSuccess(List<CommentResponse> commentResponses) throws IOException;
        void onError(String message);
    }
    public interface SaveCallback {
        void onSuccess(CommentResponse commentResponse);
        void onError(String message);
    }
    public void getCommentByPostId(Long postId, GetCommentByPostIdCallBack callback){
        Call<DataResponse<List<CommentResponse>>> call = commentApi.getCommentsByPostId(postId);
        call.enqueue(new Callback<DataResponse<List<CommentResponse>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<CommentResponse>>> call, Response<DataResponse<List<CommentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CommentResponse> commentResponses = response.body().getData();
                    try {
                        callback.onSuccess(commentResponses);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError("Lỗi phản hồi từ server");
                }
            }

            @Override
            public void onFailure(Call<DataResponse<List<CommentResponse>>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void saveComment(CreateCommentRequest commentRequest, Long postId, Context context , SaveCallback callback) {
        Call<DataResponse<CommentResponse>> call = commentApi.createComment(commentRequest,postId);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<CommentResponse>>() {

            @Override
            public void onSuccess(DataResponse<CommentResponse> response) {
                callback.onSuccess(response.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable.getMessage());
            }
        });
//        call.enqueue(new Callback<DataResponse<CommentResponse>>() {
//            @Override
//            public void onResponse(Call<DataResponse<CommentResponse>> call, Response<DataResponse<CommentResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    CommentResponse commentResponse = response.body().getData();
//                    callback.onSuccess(commentResponse);
//                } else {
//                    callback.onError("Lỗi phản hồi từ server");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataResponse<CommentResponse>> call, Throwable throwable) {
//                callback.onError("Lỗi mạng: " + throwable.getMessage());
//            }
//        });
    }
    public void saveReplies(CreateCommentRequest commentRequest, Long parentId,Context context ,SaveCallback callback) {
        Call<DataResponse<CommentResponse>> call = commentApi.createReply(commentRequest,parentId);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<CommentResponse>>() {

            @Override
            public void onSuccess(DataResponse<CommentResponse> response) {
                callback.onSuccess(response.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable.getMessage());
            }
        });
//        call.enqueue(new Callback<DataResponse<CommentResponse>>() {
//            @Override
//            public void onResponse(Call<DataResponse<CommentResponse>> call, Response<DataResponse<CommentResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    CommentResponse commentResponse = response.body().getData();
//                    callback.onSuccess(commentResponse);
//                } else {
//                    callback.onError("Lỗi phản hồi từ server");
//                }
//            }
//            @Override
//            public void onFailure(Call<DataResponse<CommentResponse>> call, Throwable throwable) {
//                callback.onError("Lỗi mạng: " + throwable.getMessage());
//            }
//        });
    }
    public void likeComment(Long commentId,Context context ,ResultCallback<Integer> callback) {
        Call<DataResponse<Integer>> call = commentApi.likeComment(commentId);
        ApiCaller.executeApiCall(call,context, new ApiCaller.ResultCallback<DataResponse<Integer>>() {
            @Override
            public void onSuccess(DataResponse<Integer> response) {
                callback.onSuccess(response.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
//        commentApi.likeComment(commentId).enqueue(new Callback<DataResponse<Integer>>() {
//            @Override
//            public void onResponse(Call<DataResponse<Integer>> call, Response<DataResponse<Integer>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Integer commentLikeCount = response.body().getData();
//                    callback.onSuccess(commentLikeCount);
//
//                } else {
//                    callback.onError(new IOException(response.message()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataResponse<Integer>> call, Throwable throwable) {
//                callback.onError(throwable);
//            }
//        });
    }
    public void getCommentById(Long commentId, ResultCallback<CommentResponse> callback) {
        commentApi.getCommentById(commentId).enqueue(new Callback<DataResponse<CommentResponse>>() {
            @Override
            public void onResponse(@NonNull Call<DataResponse<CommentResponse>> call, @NonNull Response<DataResponse<CommentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentResponse comment = response.body().getData();
                    callback.onSuccess(comment);

                } else {
                    callback.onError(new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataResponse<CommentResponse>> call, @NonNull Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
    public void getRepliesByParentId(Long parentId, ResultCallback<List<CommentResponse>> callback) {
        commentApi.getRepliesByParentId(parentId).enqueue(new Callback<DataResponse<List<CommentResponse>>>() {
            @Override
            public void onResponse(Call<DataResponse<List<CommentResponse>>> call, Response<DataResponse<List<CommentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CommentResponse> comment = response.body().getData();
                    callback.onSuccess(comment);

                } else {
                    callback.onError(new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<DataResponse<List<CommentResponse>>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
    public void getCommentsAndRepliesByCurrentUser(Context context,ResultCallback<List<CommentResponse>> callback) {
        Call<DataResponse<List<CommentResponse>>> call = commentApi.getCommentsAndRepliesByCurrentUser();
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<List<CommentResponse>>>() {

            @Override
            public void onSuccess(DataResponse<List<CommentResponse>> response) {
                callback.onSuccess(response.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
}
