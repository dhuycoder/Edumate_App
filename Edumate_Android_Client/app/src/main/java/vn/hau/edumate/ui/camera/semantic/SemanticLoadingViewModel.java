package vn.hau.edumate.ui.camera.semantic;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import vn.hau.edumate.data.model.response.SemanticSearchResponse;
import vn.hau.edumate.data.repository.SemanticSearchHistoryRepository;
import vn.hau.edumate.data.repository.SemanticSearchRepository;
import vn.hau.edumate.util.ResultCallback;

public class SemanticLoadingViewModel extends AndroidViewModel {

    private static final String TAG = SemanticLoadingViewModel.class.getSimpleName();

    private final MutableLiveData<List<SemanticSearchResponse>> postsSearchedResponse = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> responseMessage = new MutableLiveData<>();
    private final MutableLiveData<String> searchingText = new MutableLiveData<>("Đang tìm kiếm");
    private final MutableLiveData<Boolean> searching = new MutableLiveData<>(false);
    private final SemanticSearchRepository semanticSearchRepository;
    private final SemanticSearchHistoryRepository semanticSearchHistoryRepository;
    private Handler handler;
    private int dotCount = 0;

    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getResponseMessage() { return responseMessage; }
    public LiveData<List<SemanticSearchResponse>> getPostsSearchedResponse() { return postsSearchedResponse; }
    public LiveData<String> getSearchingText() { return searchingText; }

    public SemanticLoadingViewModel(@NonNull Application application) {
        super(application);
        semanticSearchRepository = new SemanticSearchRepository(application);
        semanticSearchHistoryRepository = new SemanticSearchHistoryRepository(application);
        handler = new Handler(Looper.getMainLooper());
    }

    public void semanticSearchPosts(Object object, boolean isSaveHistory) {
        startSearchingAnimation();
        searchingText.setValue("Đang tìm kiếm");
        semanticSearchRepository.semanticSearch(new ResultCallback<List<SemanticSearchResponse>>() {
            @Override
            public void onSuccess(List<SemanticSearchResponse> result) {
                stopSearchingAnimation();
                if (result != null && !result.isEmpty()) {
                    postsSearchedResponse.setValue(result);
                } else {
                    responseMessage.setValue("Không tìm thấy ảnh tương đồng !");
                    postsSearchedResponse.setValue(result);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                stopSearchingAnimation();
                errorMessage.setValue("Đã có lỗi xảy ra !");
                Log.e(TAG, "Error during semantic search: ", throwable);
            }
        }, object);

        if(isSaveHistory) {
            saveSemanticSearchHistory((Uri) object);
        }
    }

    private void saveSemanticSearchHistory(Uri uri) {
        semanticSearchHistoryRepository.saveSemanticSearchHistory(uri, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "Semantic search history saved successfully.");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Error saving semantic search history: ", throwable);
            }
        });
    }

    private void startSearchingAnimation() {
        searching.setValue(true);
        dotCount = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Boolean.TRUE.equals(searching.getValue())) return;
                dotCount = (dotCount + 1) % 4;
                StringBuilder textBuilder = new StringBuilder("Đang tìm kiếm");
                for (int i = 0; i < dotCount; i++) {
                    textBuilder.append(".");
                }
                searchingText.setValue(textBuilder.toString());
                handler.postDelayed(this, 300);
            }
        }, 300);
    }

    private void stopSearchingAnimation() {
        searching.setValue(false);
        dotCount = 0;
    }
}

