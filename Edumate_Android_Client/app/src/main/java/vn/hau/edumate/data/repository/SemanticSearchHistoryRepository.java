package vn.hau.edumate.data.repository;

import static com.facebook.FacebookSdk.getCacheDir;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
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
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.util.ResultCallback;

public class SemanticSearchHistoryRepository {
    private static final String TAG = SemanticSearchHistoryRepository.class.getSimpleName();
    private final HistorySemanticSearchApi historySemanticSearchApi;
    private final FirebaseAuth firebaseAuth;


    public SemanticSearchHistoryRepository(Context context) {
        this.historySemanticSearchApi = ApiClient.createService(HistorySemanticSearchApi.class, context);
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void saveSemanticSearchHistory(Uri imgUri, ResultCallback<Void> callback) {
        try {
            File imageFile = new File(Objects.requireNonNull(getRealPathFromURI(imgUri)));
            if (!imageFile.exists()) {
                Log.e(TAG, "File ảnh không tồn tại: " + imgUri);
                return;
            }

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.e(TAG, "Người dùng chưa đăng nhập");
                return;
            }

            RequestBody uidBody = RequestBody.create(MediaType.parse("text/plain"), user.getUid());

            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);

            /*List<MultipartBody.Part> imgIdParts = new ArrayList<>();
            for (SemanticSearchResponse response : responses) {
                RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(response.getId()));
                MultipartBody.Part part = MultipartBody.Part.createFormData("imgIds", null, idBody);
                imgIdParts.add(part);
            }*/

            historySemanticSearchApi.addNewHistorySemanticSearch(imagePart, uidBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError(new Exception("Lỗi response: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError(t);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi gọi API semantic history", e);
            callback.onError(e);
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

    public void fetchSemanticSearchHistories(ResultCallback<List<SemanticSearchHistoryResponse>> callback) {
        historySemanticSearchApi.getSemanticSearchHistories().enqueue(new Callback<List<SemanticSearchHistoryResponse>>() {
            @Override
            public void onResponse(Call<List<SemanticSearchHistoryResponse>> call, Response<List<SemanticSearchHistoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Lấy lịch sử thành công");
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Lỗi khi lấy lịch sử"));
                }
            }

            @Override
            public void onFailure(Call<List<SemanticSearchHistoryResponse>> call, Throwable t) {
                Log.e(TAG, "Lỗi khi lấy lịch sử", t);
                callback.onError(t);
            }
        });
    }

    public void deleteAllSemanticSearchHistories(ResultCallback<Void> callback) {
        historySemanticSearchApi.deleteAllHistorySemanticSearch().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Xóa tất cả lịch sử thành công");
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Lỗi khi xóa tất cả lịch sử"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi khi xóa tất cả lịch sử", t);
                callback.onError(t);
            }
        });
    }

    public void deleteSemanticSearchHistoryById(Long id, ResultCallback<Void> callback) {
        historySemanticSearchApi.deleteHistorySemanticSearchById(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Xóa lịch sử thành công");
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Lỗi khi xóa lịch sử"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi khi xóa lịch sử", t);
                callback.onError(t);
            }
        });
    }
}
