package vn.hau.edumate.ui.history.ai;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import vn.hau.edumate.data.model.response.AISearchHistoryResponse;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.SemanticSearchHistoryResponse;
import vn.hau.edumate.data.repository.AISearchHistoryRepository;
import vn.hau.edumate.util.ResultCallback;

public class HistoryAISearchViewModel extends AndroidViewModel {

    private final AISearchHistoryRepository AISearchHistoryRepository;

    private MutableLiveData<List<AISearchHistoryResponse>> historiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> sumHistory = new MutableLiveData<>(0);

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HistoryAISearchViewModel(@NonNull Application application) {
        super(application);
        AISearchHistoryRepository = new AISearchHistoryRepository(application);
    }
    public LiveData<List<AISearchHistoryResponse>> getHistoriesLiveData() {
        return historiesLiveData;
    }

    public LiveData<Integer> getSumHistory() {
        return sumHistory;
    }

    public void fetchHistories(Context context) {
        AISearchHistoryRepository.fetchAISearchHistories(context,new ResultCallback<List<AISearchHistoryResponse>>() {
            @Override
            public void onSuccess(List<AISearchHistoryResponse> result) {
                historiesLiveData.setValue(result);
                sumHistory.setValue(result != null ? result.size() : 0);
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue(throwable.getMessage());
            }
        });
    }

    public void deleteHistory(Context context,Long historyId) {
        AISearchHistoryRepository.deleteAISearchHistoryById(context,historyId, new ResultCallback<DataResponse<Void>>() {
            @Override
            public void onSuccess(DataResponse<Void> result) {
                if (historiesLiveData.getValue() != null) {
                    List<AISearchHistoryResponse> currentList = new ArrayList<>(historiesLiveData.getValue());
                    currentList.removeIf(history -> history.getId().equals(historyId));
                    historiesLiveData.setValue(currentList);
                    sumHistory.setValue(currentList.size());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue(throwable.getMessage());
            }
        });
    }

    public void deleteAllHistories(Context context) {
        AISearchHistoryRepository.deleteAllAISearchHistories(context,new ResultCallback<DataResponse<Void>>() {
            @Override
            public void onSuccess(DataResponse<Void> result) {
                historiesLiveData.setValue(new ArrayList<>());
                sumHistory.setValue(0);
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue(throwable.getMessage());
            }
        });
    }

}
