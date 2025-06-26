package vn.hau.edumate.data.repository;


import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hau.edumate.data.api.ApiCaller;
import vn.hau.edumate.data.api.image.ImageApi;

import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;

import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.util.ResultCallback;

public class ImageRepository {
    private final ImageApi imageApi;

    public ImageRepository(ImageApi imageApi) {
        this.imageApi = imageApi;
    }


    public void saveImage(List<MultipartBody.Part> multipartFiles, Context context, ResultCallback<DataResponse<List<ImageResponse>>> callback) {
        Call<DataResponse<List<ImageResponse>>> call = imageApi.saveImage(multipartFiles);
        ApiCaller.executeApiCall(call, context, new ApiCaller.ResultCallback<DataResponse<List<ImageResponse>>>() {
            @Override
            public void onSuccess(DataResponse<List<ImageResponse>> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError( new IOException(throwable));
            }
        });
//        imageApi.saveImage(multipartFiles).enqueue(new Callback<>() {
//
//            @Override
//            public void onResponse(Call<DataResponse<List<ImageResponse>>> call, Response<DataResponse<List<ImageResponse>>> response) {
//                if(response.body() != null){
//                    DataResponse<List<ImageResponse>> dataResponse = response.body();
//                    if (response.isSuccessful()) {
//
//                    } else {
//
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DataResponse<List<ImageResponse>>> call, Throwable throwable) {
//
//            }
//        });
    }
}
