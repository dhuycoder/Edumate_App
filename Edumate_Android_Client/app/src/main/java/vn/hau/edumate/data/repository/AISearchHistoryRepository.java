package vn.hau.edumate.data.repository;

import static com.facebook.FacebookSdk.getCacheDir;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.R;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.history.HistoryAISearchApi;
import vn.hau.edumate.data.model.response.AISearchHistoryResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.util.ResultCallback;

public class AISearchHistoryRepository {
    private static final String TAG = "HistoryRepository";
    private final HistoryAISearchApi historyAISearchApi;
    private final FirebaseAuth firebaseAuth;

    public AISearchHistoryRepository(Context context) {
        historyAISearchApi = ApiClient.createService(HistoryAISearchApi.class, context);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void saveAISearchHistory(Context context,Uri imgUri, String answer, ResultCallback<DataResponse<Void>> callback) {

            File imageFile = new File(Objects.requireNonNull(getRealPathFromURI(imgUri)));

            if (!imageFile.exists()) {
                Log.e(TAG, "File ảnh không tồn tại: ");
                return;
            }
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Log.d("HistoryRepository", "User: " + user);
            Log.d("HistoryRepository", "User.getUid(): " + user.getUid());

            RequestBody uidBody = RequestBody.create(MediaType.parse("text/plain"), Objects.requireNonNull(user).getUid());
            RequestBody answerBody = RequestBody.create(MediaType.parse("text/plain"), answer);

            RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), imageRequestBody);
            Call call = historyAISearchApi.addNewHistoryAISearch(imagePart, uidBody, answerBody);
            ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<Void>>() {
                @Override
                public void onSuccess( DataResponse<Void> response) {
                    callback.onSuccess(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    callback.onError(throwable);
                }
            });

//            historyAISearchApi.addNewHistoryAISearch(imagePart, uidBody, answerBody).enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    callback.onSuccess(response.body());
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                    callback.onError(t);
//                }
//            });


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

    public void fetchAISearchHistories(Context context, ResultCallback<List<AISearchHistoryResponse>> callback) {
        Call call = historyAISearchApi.getAISearchHistories();
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<List<AISearchHistoryResponse>>() {
            @Override
            public void onSuccess(List<AISearchHistoryResponse> response) {
                Log.d(TAG, "Lấy lịch sử thành công");
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(new Exception("Lỗi khi lấy lịch sử"));
            }
        });

//        historyAISearchApi.getAISearchHistories().enqueue(new Callback<List<AISearchHistoryResponse>>() {
//            @Override
//            public void onResponse(Call<List<AISearchHistoryResponse>> call, Response<List<AISearchHistoryResponse>> response) {
//                if(response.isSuccessful() && response.body() != null){
//                    Log.d(TAG, "Lấy lịch sử thành công");
//                    callback.onSuccess(response.body());
//                }
//                else {
//                    callback.onError(new Exception("Lỗi khi lấy lịch sử"));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<AISearchHistoryResponse>> call, Throwable t) {
//                Log.e(TAG, "Lỗi khi lấy lịch sử", t);
//                callback.onError(new Exception("Lỗi khi lấy lịch sử"));
//            }
//        });
    }

    public void deleteAllAISearchHistories(Context context,ResultCallback<DataResponse<Void>> callback) {
        Call call = historyAISearchApi.deleteAllHistoryAISearch();
        ApiCaller.executeApiCall(call,context, new ApiCaller.ResultCallback<DataResponse<Void>>() {

            @Override
            public void onSuccess(DataResponse<Void> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
//        historyAISearchApi.deleteAllHistoryAISearch().enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    callback.onError(new Exception("Lỗi response: " + response.code()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                callback.onError(t);
//            }
//        });
    }

    public void deleteAISearchHistoryById(Context context, Long id, ResultCallback<DataResponse<Void>> callback) {
        Call call = historyAISearchApi.deleteHistoryAISearchById(id);
        ApiCaller.executeApiCall(call,context, new ApiCaller.ResultCallback<DataResponse<Void>>() {

            @Override
            public void onSuccess(DataResponse<Void> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });

//        historyAISearchApi.deleteHistoryAISearchById(id).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    callback.onSuccess(response.body());
//                } else {
//                    callback.onError(new Exception("Lỗi response: " + response.code()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                callback.onError(t);
//            }
//        });
    }
}
