package vn.hau.edumate.data.api.search;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;

public interface SemanticSearchApi {

    @Multipart
    @POST("/api/v1/semantic-search")
    Call<List<SemanticSearchResponse>> semanticSearch(@Part MultipartBody.Part file);
}
