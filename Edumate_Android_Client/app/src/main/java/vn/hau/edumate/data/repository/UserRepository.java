package vn.hau.edumate.data.repository;

import android.content.Context;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.user.UserApi;
import vn.hau.edumate.data.model.request.CreateUserStatusHistory;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;
import vn.hau.edumate.util.ResultCallback;

public class UserRepository {
    private final UserApi userApi;

    public UserRepository(UserApi userApi) {
        this.userApi = userApi;
    }
    public void createUserStatusHistory(String userId, CreateUserStatusHistory request, ResultCallback<DataResponse<Integer>> callback) {
        userApi.createUserStatusHistory(userId,request).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call<DataResponse<Integer>> call, Response<DataResponse<Integer>> response) {
                if( response.isSuccessful() && response.body() != null){
                    callback.onSuccess(response.body());
                }
                else{
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
            public void onFailure(Call<DataResponse<Integer>> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
    public void getCurrentUser(Context context, ResultCallback<DataResponse<UserResponse>> callback) {
        Call<DataResponse<UserResponse>> call = userApi.getCurrentUser();
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<UserResponse>>() {

            @Override
            public void onSuccess(DataResponse<UserResponse> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
    public void updateUser(Context context, RequestBody username, RequestBody status,MultipartBody.Part file, ResultCallback<DataResponse<UserResponse>> callback) {
        Call<DataResponse<UserResponse>> call = userApi.updateUser(username,status,file);
        ApiCaller.executeApiCall(call,context ,new ApiCaller.ResultCallback<DataResponse<UserResponse>>() {

            @Override
            public void onSuccess(DataResponse<UserResponse> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });

    }
}
