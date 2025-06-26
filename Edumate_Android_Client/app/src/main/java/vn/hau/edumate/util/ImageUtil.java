package vn.hau.edumate.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageUtil {
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    public static List<MultipartBody.Part> getMultipleImageParts(List<Uri> imageUris, Context context) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        for (Uri uri : imageUris) {
            try {
                byte[] bytes;

                if (uri.toString().startsWith("http")) {
                    // Mở kết nối và tải ảnh từ URL
                    URL url = new URL(uri.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream inputStream = connection.getInputStream();
                    bytes = getBytes(inputStream);
                    connection.disconnect();
                } else {
                    // Lấy ảnh từ local (content:// hoặc file://)
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    bytes = getBytes(inputStream);
                }

                RequestBody requestFile = RequestBody.create(
                        bytes,
                        MediaType.parse("image/jpeg") // hoặc "image/png" nếu bạn biết rõ định dạng
                );

                MultipartBody.Part part = MultipartBody.Part.createFormData(
                        "multipartFiles",
                        "upload.jpg",
                        requestFile
                );

                parts.add(part);

            } catch (IOException e) {
                Log.e("UploadError", "Error uploading image: " + uri.toString(), e);
            }
        }

        return parts;
    }
}
