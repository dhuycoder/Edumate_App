package vn.hau.edumate.ui.camera.ai;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.repository.AISearchHistoryRepository;
import vn.hau.edumate.helper.GeminiHelper;
import vn.hau.edumate.util.ResultCallback;

public class AIResultViewModel extends AndroidViewModel {
    private final MutableLiveData<String> response = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private GeminiHelper helper;
    private AISearchHistoryRepository AISearchHistoryRepository;

    public AIResultViewModel(@NonNull Application application) {
        super(application);
        AISearchHistoryRepository = new AISearchHistoryRepository(application);
    }

    public LiveData<String> getResponse() {
        return response;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void processImageAndAsk(Context context, Uri imageUri) {
        if (imageUri == null) {
            response.postValue("URI ảnh không hợp lệ");
            loading.postValue(false);
            return;
        }

        loading.setValue(true);

        new Thread(() -> {
            try {
                if (context == null) {
                    response.postValue("Context không hợp lệ");
                    loading.postValue(false);
                    return;
                }

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                if (bitmap == null) {
                    response.postValue("Không thể đọc ảnh");
                    loading.postValue(false);
                    return;
                }

                List<Bitmap> images = Collections.singletonList(bitmap);

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        GeminiHelper.getInstance(context)
                                .getAnswer(images, getPrompt())
                                .observe((LifecycleOwner) context, result -> {
                                    response.setValue(result);

                                    if (result.equals("0")) {
                                        response.setValue("Ảnh không hợp lệ. Vui lòng thử lại với ảnh khác.");
                                    } else {
                                        AISearchHistoryRepository.saveAISearchHistory(context,imageUri, result, new ResultCallback<DataResponse<Void>>() {
                                            @Override
                                            public void onSuccess(DataResponse<Void> result) {
                                                Log.d("AIResultViewModel", "Lưu lịch sử thành công");
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                Log.e("AIResultViewModel", "Lỗi khi lưu lịch sử: " + throwable.getMessage());
                                            }
                                        });
                                    }
                                    loading.setValue(false);
                                });
                    } catch (Exception e) {
                        response.setValue("Lỗi khi gọi AI: " + e.getMessage());
                        loading.setValue(false);
                    }
                });

            } catch (IOException e) {
                response.postValue("Không thể xử lý ảnh: " + e.getMessage());
                loading.postValue(false);
            } catch (Exception e) {
                response.postValue("Lỗi không xác định: " + e.getMessage());
                loading.postValue(false);
            }
        }).start();
    }

    private String getPrompt() {
        return "Phân tích hình ảnh này và chỉ trả lời nếu ảnh nằm trong các nội dung sau: " +
                "Nếu đây là bài toán (toán, lý, hóa, sinh,...), hãy giải từng bước một cách chi tiết, dùng các ký hiệu toán học chuẩn. " +
                "Nếu đây là câu hỏi lý thuyết, hay một câu hỏi khác, hãy giải thích khái niệm và đưa ra ví dụ cụ thể." +
                "Nếu không phải là ảnh về câu hỏi, bài tập, kiến thức,... mà là ảnh linh tinh không liên quan thì bạn từ chối, nói khéo cho người dùng biết là được." +
                "Nếu ảnh có nội dung đồi trụy phản cảm, bạo lực, máu me, kích động, phản động, việt nam cộng hòa, liên quan đến chế độ cũ, đối lập chính trị ở việt nam, ảnh sex, tình dục trẻ em," +
                " ảnh khiêu dâm, ảnh khỏa thân, ảnh khiêu dâm trẻ,.... thì trả lời là: 0";
    }
}
