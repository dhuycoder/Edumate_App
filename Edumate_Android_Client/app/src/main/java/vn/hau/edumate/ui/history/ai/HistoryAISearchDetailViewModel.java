package vn.hau.edumate.ui.history.ai;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import vn.hau.edumate.data.model.response.AISearchHistoryResponse;

public class HistoryAISearchDetailViewModel extends ViewModel {

    @Getter
    private final MutableLiveData<AISearchHistoryResponse> historyLiveData = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setHistory(Intent intent) {
        if(intent != null && intent.hasExtra("history")) {
            AISearchHistoryResponse history = intent.getParcelableExtra("history");
            historyLiveData.setValue(history);
        } else {
            errorMessage.setValue("No history found");
        }
    }
}
