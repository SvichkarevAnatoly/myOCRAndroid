package ru.myocr.util;


import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BitmapUtil {
    public static MultipartBody.Part buildMultipartBody(Bitmap bmp, String paramName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] inputImage = stream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), inputImage);
        return MultipartBody.Part.createFormData(paramName, paramName, requestFile);
    }
}
