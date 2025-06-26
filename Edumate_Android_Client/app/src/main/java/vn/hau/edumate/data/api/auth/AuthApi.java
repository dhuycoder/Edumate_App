package vn.hau.edumate.data.api.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import vn.hau.edumate.data.model.request.FirebaseTokenRequest;
import vn.hau.edumate.data.model.response.TokenResponse;

public interface AuthApi {
    @POST("/v1/auth/firebase")
    @Headers("No-Auth: true")
    Call<TokenResponse> authenticate(@Body FirebaseTokenRequest request);

    @POST("/v1/auth/firebase/refresh-token")
    @Headers("No-Auth: true")
    Call<TokenResponse> getRefreshToken(@Body FirebaseTokenRequest request);

    @POST("/logout")
    Call<Void> logout();
}
