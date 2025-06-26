package vn.hau.edumate.data.api.user;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import vn.hau.edumate.data.model.request.CreateUserStatusHistory;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.UserResponse;

public interface UserApi {
    @POST("/user/{userId}/status-histories")
    Call<DataResponse<Integer>> createUserStatusHistory(@Path("userId") String userId, @Body CreateUserStatusHistory request);
    @GET("user/current-user")
    Call<DataResponse<UserResponse>> getCurrentUser();
    @Multipart
    @PUT("user")
    Call<DataResponse<UserResponse>> updateUser(
            @Part("username") RequestBody username,
            @Part("status") RequestBody status,
            @Part MultipartBody.Part multipartFiles
    );
}
