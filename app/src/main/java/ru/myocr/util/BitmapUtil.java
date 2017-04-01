package ru.myocr.util;


import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.myocr.App;

public class BitmapUtil {
    public static MultipartBody.Part buildMultipartBody(Bitmap bmp, String paramName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] inputImage = stream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), inputImage);
        return MultipartBody.Part.createFormData(paramName, paramName, requestFile);
    }

    public static File createTempFile() throws IOException {
        // Create an image file name
        String timeStamp = TimeUtil.DATE_FORMAT.format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = App.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
}
