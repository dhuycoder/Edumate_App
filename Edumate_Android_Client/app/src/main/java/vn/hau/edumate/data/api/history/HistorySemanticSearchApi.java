package vn.hau.edumate.data.api.history;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;

public interface HistorySemanticSearchApi {
    @Multipart
    @POST("/history/semantic-search")
    Call<Void> addNewHistorySemanticSearch(
            @Part MultipartBody.Part image,
            @Part("uid") RequestBody uid
            /*@Part List<MultipartBody.Part> imgIds*/
    );

    @GET("/history/semantic-search/image/{id}")
    Call<ResponseBody> downloadImage(@Path("id") Long id);

    @GET("/history/semantic-search")
    Call<List<SemanticSearchHistoryResponse>> getSemanticSearchHistories();

    @DELETE("/history/semantic-search")
    Call<Void> deleteAllHistorySemanticSearch();

    @DELETE("/history/semantic-search/{id}")
    Call<Void> deleteHistorySemanticSearchById(@Path("id") Long id);
}
