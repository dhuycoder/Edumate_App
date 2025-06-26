package vn.hau.edumate.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.Executor;

public class GeminiHelper {
    private static volatile GeminiHelper INSTANCE;

    private final GenerativeModelFutures generativeModelFutures;

    private final Executor executor;

    private static final String API_KEY = "AIzaSyCAlW2Qz-Csc4nXQ8n5cduNlnjOIjDDJ4A";

    private static final String MODEL = "gemini-2.0-flash";

    private GeminiHelper(Context context) {
        GenerativeModel gm = new GenerativeModel(MODEL, API_KEY);
        this.generativeModelFutures = GenerativeModelFutures.from(gm);
        this.executor = ContextCompat.getMainExecutor(context);
    }

    public static GeminiHelper getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (GeminiHelper.class) {
                if(INSTANCE == null) {
                    INSTANCE = new GeminiHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<String> getAnswer(List<Bitmap> images, String prompt) {
        MutableLiveData<String> resultLiveData = new MutableLiveData<>();
        Content.Builder builder = new Content.Builder().addText(prompt);
        if(images != null){
            for (Bitmap image : images) {
                builder.addImage(image);
            }
        }

        Content content = builder.build();

        ListenableFuture<GenerateContentResponse> response = generativeModelFutures.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String text = result.getText();
                if (text != null && !text.isEmpty()) {
                    resultLiveData.postValue(text);
                } else {
                    resultLiveData.postValue("Không có phản hồi từ Gemini.");
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("GeminiHelper", "Lỗi khi gọi Gemini: " + t.getMessage());
                resultLiveData.postValue("Lỗi khi gọi Gemini: " + t.getMessage());
            }
        }, executor);

        return resultLiveData;
    }
    public Bitmap createSampleBitmap() {
        int width = 512;
        int height = 512;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Vẽ nền màu vàng nhạt
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#FFFACD")); // LemonChiffon (vàng nhạt)
        canvas.drawRect(0, 0, width, height, paint);

        // Vẽ hình tròn màu đỏ ở giữa
        paint.setColor(Color.RED);
        float centerX = width / 2f;
        float centerY = height / 2f;
        float radius = 100;
        canvas.drawCircle(centerX, centerY - 100, radius, paint);

        // Vẽ chữ mô tả
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Ảnh mẫu mới", centerX, centerY + 100, paint);

        return bitmap;
    }


}