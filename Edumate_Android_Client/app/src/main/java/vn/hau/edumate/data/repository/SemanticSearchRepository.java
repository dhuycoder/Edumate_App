package vn.hau.edumate.data.repository;

import static com.facebook.FacebookSdk.getCacheDir;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.history.HistorySemanticSearchApi;
import vn.hau.edumate.data.api.search.SemanticSearchApi;
import vn.hau.edumate.data.constant.SystemConstant;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.util.ResultCallback;

public class SemanticSearchRepository {
    private static final String TAG = SemanticSearchRepository.class.getSimpleName();
    private final SemanticSearchApi semanticSearchApi;
    private final HistorySemanticSearchApi historySemanticSearchApi;
    public SemanticSearchRepository(Context context) {
        this.semanticSearchApi = ApiClient.createService(SemanticSearchApi.class, context, SystemConstant.SEMANTIC_SEARCH_SERVICE_URL);
        this.historySemanticSearchApi = ApiClient.createService(HistorySemanticSearchApi.class, context);
    }

    public void semanticSearch(ResultCallback<List<SemanticSearchResponse>> callback, Object object) {
        if(object instanceof Uri imgUri) {
            try{
                File imageFile = new File(Objects.requireNonNull(getRealPathFromURI(imgUri)));

                if (!imageFile.exists()) {
                    Log.e(TAG, "File ảnh không tồn tại: ");
                    return;
                }

                RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                MultipartBody.Part file = MultipartBody.Part.createFormData("file", imageFile.getName(), imageRequestBody);

                semanticSearchApi.semanticSearch(file).enqueue(new Callback<List<SemanticSearchResponse>>() {
                    @Override
                    public void onResponse(Call<List<SemanticSearchResponse>> call, Response<List<SemanticSearchResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e(TAG, "Request failed. Status: " + response.code() + ", Error: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            callback.onError(new Exception("Failed to fetch posts"));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SemanticSearchResponse>> call, Throwable t) {
                        callback.onError(t);
                    }
                });
            }catch (Exception e){
                Log.e(TAG, "Lỗi: ", e);
            }
        }
        else if (object instanceof Long imageId) {
            Log.d(TAG, "Image ID provided: " + imageId);
            historySemanticSearchApi.downloadImage(imageId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        try {
                            byte[] imageBytes = response.body().bytes();
                            RequestBody reqFile = RequestBody.create(
                                    imageBytes,
                                    MediaType.parse("image/jpeg")
                            );
                            MultipartBody.Part file = MultipartBody.Part.createFormData(
                                    "file", "image.jpg", reqFile
                            );
                            semanticSearchApi.semanticSearch(file).enqueue(new Callback<List<SemanticSearchResponse>>() {
                                @Override
                                public void onResponse(Call<List<SemanticSearchResponse>> call, Response<List<SemanticSearchResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        callback.onSuccess(response.body());
                                    } else {
                                        try {
                                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                            Log.e(TAG, "Request failed. Status: " + response.code() + ", Error: " + errorBody);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        callback.onError(new Exception("Failed to fetch posts"));
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<SemanticSearchResponse>> call, Throwable t) {
                                    callback.onError(t);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onError(t);
                }
            });

        } else {
            Log.e(TAG, "Invalid object type for semantic search");
        }
    }

    private String getRealPathFromURI(Uri uri) {
        try {
            if ("file".equals(uri.getScheme())) {
                return uri.getPath();
            }

            if ("content".equals(uri.getScheme())) {
                String path = uri.getPath();
                if (path != null && path.contains("/cache/")) {
                    return getCacheDir().getAbsolutePath() + "/" +
                            path.substring(path.lastIndexOf("/") + 1);
                }
            }

            return uri.getPath();
        } catch (Exception e) {
            return null;
        }
    }
}
