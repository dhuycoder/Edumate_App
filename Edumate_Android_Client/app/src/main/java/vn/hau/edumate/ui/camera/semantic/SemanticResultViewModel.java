package vn.hau.edumate.ui.camera.semantic;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Objects;

import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.util.ResultCallback;

public class SemanticResultViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private final MutableLiveData<Long> postIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<SemanticSearchResponse>> semanticSearchResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Uri> imageUriLiveData = new MutableLiveData<>();
    private final MutableLiveData<SemanticSearchHistoryResponse> semanticSearchResponseLiveData = new MutableLiveData<>();
    public SemanticResultViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository(ApiClient.createService(PostApi.class, application));
    }

    public LiveData<SemanticSearchHistoryResponse> getSemanticSearchResponseLiveData() {
        return semanticSearchResponseLiveData;
    }

    public LiveData<Long> getPostIdLiveData() {
        return postIdLiveData;
    }

    public LiveData<Uri> getImageUriLiveData() {
        return imageUriLiveData;
    }

    public LiveData<ArrayList<SemanticSearchResponse>> getSemanticSearchResultsLiveData() {
        return semanticSearchResultsLiveData;
    }

    public void getListResult(Intent intent) {
        ArrayList<SemanticSearchResponse> semanticSearchResults = intent.getParcelableArrayListExtra("imgList");
        if (semanticSearchResults == null) {
            Log.e("SemanticResultViewModel", "No image URI found in intent extras.");
        }
        Log.d("SemanticResultViewModel", "Received semantic search results: " + semanticSearchResults);
        semanticSearchResultsLiveData.setValue(Objects.requireNonNullElseGet(semanticSearchResults, ArrayList::new));
    }

    public void getUri(Intent intent) {
        Uri imageUri = intent.getParcelableExtra("resultUri");
        if (imageUri == null) {
            Log.e("SemanticResultViewModel", "No image URI found in intent extras.");
        } else {
            Log.d("SemanticResultViewModel", "Received image URI: " + imageUri);
            imageUriLiveData.setValue(imageUri);
        }
    }

    public void getResponse(Intent intent) {
        SemanticSearchHistoryResponse semanticSearchResponse = intent.getParcelableExtra("response");
        if (semanticSearchResponse == null) {
            Log.e("SemanticResultViewModel", "No semantic search response found in intent extras.");
        } else {
            Log.d("SemanticResultViewModel", "Received semantic search response: " + semanticSearchResponse);
            semanticSearchResponseLiveData.setValue(semanticSearchResponse);
        }
    }

    public void fetchPostId(Long id) {
        postRepository.fetchPostIdByImageId(id, new ResultCallback<DataResponse<Long>>() {

            @Override
            public void onSuccess(DataResponse<Long> result) {
                postIdLiveData.setValue(result.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("SemanticResultViewModel", "Error fetching post by img ID: " + throwable.getMessage());
            }
        });
    }
}
