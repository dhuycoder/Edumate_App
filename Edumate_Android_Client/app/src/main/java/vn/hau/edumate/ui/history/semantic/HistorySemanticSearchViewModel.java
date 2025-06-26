package vn.hau.edumate.ui.history.semantic;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.data.repository.SemanticSearchHistoryRepository;
import vn.hau.edumate.util.ResultCallback;

public class HistorySemanticSearchViewModel extends AndroidViewModel {
    private final SemanticSearchHistoryRepository semanticSearchHistoryRepository;
    private final MutableLiveData<List<SemanticSearchHistoryResponse>> historiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> sumHistory = new MutableLiveData<>(0);
    public HistorySemanticSearchViewModel(@NonNull Application application) {
        super(application);
        semanticSearchHistoryRepository = new SemanticSearchHistoryRepository(application);
    }

    public LiveData<List<SemanticSearchHistoryResponse>> getHistoriesLiveData() {
        return historiesLiveData;
    }

    public LiveData<Integer> getSumHistory() {
        return sumHistory;
    }

    public void fetchHistories() {
        semanticSearchHistoryRepository.fetchSemanticSearchHistories(new ResultCallback<List<SemanticSearchHistoryResponse>>() {
            @Override
            public void onSuccess(List<SemanticSearchHistoryResponse> result) {
                if (result == null || result.isEmpty()) {
                    Log.d("HistorySemanticSearchVM", "No semantic search histories found.");
                } else {
                    Log.d("HistorySemanticSearchVM", "Fetched " + result.size() + " semantic search histories.");
                }
                historiesLiveData.setValue(result != null ? new ArrayList<>(result) : new ArrayList<>());
                sumHistory.setValue(result != null ? result.size() : 0);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("HistorySemanticSearchVM", "Error fetching semantic search histories: " + throwable.getMessage());
            }
        });
    }

    public void deleteHistory(Long historyId) {
        semanticSearchHistoryRepository.deleteSemanticSearchHistoryById(historyId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("HistorySemanticSearchVM", "Deleted semantic search history with ID: " + historyId);
                if (historiesLiveData.getValue() != null) {
                    List<SemanticSearchHistoryResponse> currentList = new ArrayList<>(historiesLiveData.getValue());
                    currentList.removeIf(history -> history.getId().equals(historyId));
                    historiesLiveData.setValue(currentList);
                    sumHistory.setValue(currentList.size());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("HistorySemanticSearchVM", "Error deleting semantic search history: " + throwable.getMessage());
            }
        });
    }

    public void deleteAllHistories() {
        semanticSearchHistoryRepository.deleteAllSemanticSearchHistories(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("HistorySemanticSearchVM", "Deleted all semantic search histories.");
                historiesLiveData.setValue(new ArrayList<>());
                sumHistory.setValue(0);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("HistorySemanticSearchVM", "Error deleting all semantic search histories: " + throwable.getMessage());
            }
        });
    }
}
