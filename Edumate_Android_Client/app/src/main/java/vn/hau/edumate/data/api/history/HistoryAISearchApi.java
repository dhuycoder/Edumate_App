package vn.hau.edumate.data.api.history;


import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import vn.hau.edumate.data.model.response.AISearchHistoryResponse;
import vn.hau.edumate.data.model.response.DataResponse;

public interface HistoryAISearchApi {
    @Multipart
    @POST("/history/ai-search")
    Call<DataResponse<Void>> addNewHistoryAISearch(
            @Part MultipartBody.Part image,
            @Part("uid") RequestBody uid,
            @Part("answer") RequestBody answer
    );

    @GET("/history/ai-search/all/history")
    Call<List<AISearchHistoryResponse>> getAISearchHistories();

    @DELETE("/history/ai-search")
    Call<DataResponse<Void>> deleteAllHistoryAISearch();

    @DELETE("/history/ai-search/{id}")
    Call<DataResponse<Void>> deleteHistoryAISearchById(@Path("id") Long id);
}
