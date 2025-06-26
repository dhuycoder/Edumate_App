package vn.hau.edumate.data.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.ui.auth.LoginActivity;

public class ApiCaller {

    // Hàm tổng quát để gọi API với tùy chọn hiển thị dialog khi có lỗi
    public static <T> void executeApiCall(Call<T> call, Context context, ResultCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null ) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage;
                    if (response.code() == 403 && response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            JSONObject json = new JSONObject(errorBody);
                            errorMessage = json.optString("message", "Tài khoản của bạn đã bị khóa");

                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMessage = "Lỗi khi đọc thông báo lỗi";
                        }
                    } else {
                        String errorBody = null;
                        try {
                            if(response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                                JSONObject json = new JSONObject(errorBody);
                                errorMessage = json.getString("message");
                            }
                            else{
                                errorMessage = null;
                            }

                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    // Tự động hiển thị AlertDialog
                    if (context != null) {
                        if(errorMessage != null){
                            showErrorDialog(context, errorMessage);
                        }
                    }

                    // Gọi callback để thông báo lỗi
                    callback.onError(new IOException(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                String errorMessage = throwable.getMessage() != null ? throwable.getMessage() : "Lỗi kết nối mạng";

                // Tự động hiển thị AlertDialog
                if (context != null) {
                    showErrorDialog(context, errorMessage);
                }

                // Gọi callback để thông báo lỗi
                callback.onError(throwable);
            }
        });
    }

    // Hàm hiển thị AlertDialog
    private static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Thông báo")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if(message.startsWith("Tài khoản")){
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                })
                .setCancelable(true)
                .show();
    }

    // Interface callback để xử lý kết quả
    public interface ResultCallback<T> {
        void onSuccess(T response);
        void onError(Throwable throwable);
    }
}
