package vn.hau.edumate.data.api.image;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.ImageResponse;

public interface ImageApi {
    @Multipart
    @POST("image")
    Call<DataResponse<List<ImageResponse>>> saveImage(@Part List<MultipartBody.Part> multipartFiles);
}
