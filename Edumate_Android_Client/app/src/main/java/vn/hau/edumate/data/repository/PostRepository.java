package vn.hau.edumate.data.repository;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.request.CreatePostRequest;
import vn.hau.edumate.data.model.request.ReportRequest;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.PagedResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.util.ResultCallback;


public class PostRepository {
    private final PostApi postApi;

    public PostRepository(PostApi postApi) {
        this.postApi = postApi;
    }

    public interface GetPostCallBack {
        void onSuccess(LinkedHashSet<PostResponse> postResponses);

        void onError(Throwable message);
    }

    public interface GetPostByTagCallBack {
        void onSuccess(LinkedHashSet<PostResponse> postResponses);

        void onError(String message);
    }

    public interface GetPostByIdCallBack {
        void onSuccess(PostResponse postResponse) throws IOException;

        void onError(String message);
    }

    public interface LikePostCallBack {
        void onSuccess(Integer likeCount) throws IOException;

        void onError(String message);
    }

    public void getPostByTagType(TagType tagType,int page,int limit , Context context, GetPostCallBack callback) {
        Call<DataResponse<PagedResponse<PostResponse>>> call = postApi.getPostByTagType(tagType,page,limit);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<PagedResponse<PostResponse>>>() {

            @Override
            public void onSuccess(DataResponse<PagedResponse<PostResponse>> response) {
                callback.onSuccess(response.getData().getContent());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void likePost(Long postId, Context context, LikePostCallBack callback) {
        Call<DataResponse<Integer>> call = postApi.likePost(postId);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<Integer>>() {
            @Override
            public void onSuccess(DataResponse<Integer> response) {
                try {
                    callback.onSuccess(response.getData());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable.getMessage());
            }
        });
    }

    public void getPostByTag(Long tagId, Context context, GetPostByTagCallBack callback) {
        Call<DataResponse<LinkedHashSet<PostResponse>>> call = postApi.getPostByTag(tagId);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<LinkedHashSet<PostResponse>>>() {

            @Override
            public void onSuccess(DataResponse<LinkedHashSet<PostResponse>> response) {
                callback.onSuccess(response.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable.getMessage());
            }
        });
    }

    public void getPostById(Long postId, GetPostByIdCallBack callback) {
        Call<DataResponse<PostResponse>> call = postApi.getPostById(postId);
        call.enqueue(new Callback<DataResponse<PostResponse>>() {
            @Override
            public void onResponse(Call<DataResponse<PostResponse>> call, Response<DataResponse<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostResponse postResponses = response.body().getData();
                    try {
                        callback.onSuccess(postResponses);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError("Lỗi phản hồi từ server");
                }
            }

            @Override
            public void onFailure(Call<DataResponse<PostResponse>> call, Throwable throwable) {
                callback.onError("Lỗi mạng: " + throwable.getMessage());
            }
        });
    }

    public void createPost(CreatePostRequest createPostRequest, Context context, ResultCallback<DataResponse<PostResponse>> callback) {
        Call<DataResponse<PostResponse>> call = postApi.createPost(createPostRequest);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<PostResponse>>() {
                    @Override
                    public void onSuccess(DataResponse<PostResponse> response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.onError(throwable);
                    }
                }
        );
    }

    public void getPostByUserId(String userId, Context context, ResultCallback<DataResponse<LinkedHashSet<PostResponse>>> callback) {
        Call<DataResponse<LinkedHashSet<PostResponse>>> call = postApi.getPostByUserId(userId);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<LinkedHashSet<PostResponse>>>() {

            @Override
            public void onSuccess(DataResponse<LinkedHashSet<PostResponse>> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void getPostByCurrentUserLike(Context context, ResultCallback<DataResponse<LinkedHashSet<PostResponse>>> callback) {
        Call<DataResponse<LinkedHashSet<PostResponse>>> call = postApi.getPostByCurrentUserLike();
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<LinkedHashSet<PostResponse>>>() {
            @Override
            public void onSuccess(DataResponse<LinkedHashSet<PostResponse>> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void deletePost(Long postId, ResultCallback<DataResponse<PostResponse>> callback) {
        postApi.deletePost(postId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResponse<PostResponse>> call, Response<DataResponse<PostResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new IOException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<DataResponse<PostResponse>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void reportPost(Long postId, ReportRequest reason, ResultCallback<DataResponse<Void>> callback) {
        postApi.reportPost(postId, reason).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResponse<Void>> call, Response<DataResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Đã xảy ra lỗi.";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<DataResponse<Void>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void hidePost(Long postId, ResultCallback<DataResponse<Void>> callback) {
        postApi.hidePost(postId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResponse<Void>> call, Response<DataResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Đã xảy ra lỗi.";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<DataResponse<Void>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void fetchPostIdByImageId(Long id, ResultCallback<DataResponse<Long>> callback) {
        postApi.getPostIdByImageId(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<DataResponse<Long>> call, Response<DataResponse<Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Đã xảy ra lỗi.";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorJson);
                            if (jsonObject.has("message")) {
                                errorMessage = jsonObject.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<DataResponse<Long>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
}

