package vn.hau.edumate.data.api.tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Tag;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.request.FirebaseTokenRequest;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.TagResponse;
import vn.hau.edumate.data.model.response.TokenResponse;

public interface TagApi {
    @GET("tag/type/{type}")
    @Headers("No-Auth: true")
    Call<DataResponse<List<TagResponse>>> getTagByType(@Path("type") TagType type);
}
