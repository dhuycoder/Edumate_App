package vn.hau.edumate.data.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.tag.TagApi;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.TagResponse;

public class TagRepository {
    private final TagApi tagApi;


    public TagRepository(TagApi tagApi) {
        this.tagApi = tagApi;

    }
    public interface getTagByType {
        void onSuccess(List<TagResponse> tagResponses) ;
        void onError(String message);
    }
    public void getTagsByTagType(TagType tagType, Context context , getTagByType callback) {

        Call<DataResponse<List<TagResponse>>> call = tagApi.getTagByType(tagType);

        ApiCaller.executeApiCall(call,context, new ApiCaller.ResultCallback<DataResponse<List<TagResponse>>>() {

            @Override
            public void onSuccess(DataResponse<List<TagResponse>> response) {
                callback.onSuccess(response.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable.getMessage());
            }
        });
    }
}
