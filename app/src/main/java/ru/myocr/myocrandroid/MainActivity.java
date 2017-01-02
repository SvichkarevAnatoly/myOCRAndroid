package ru.myocr.myocrandroid;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;

import ru.myocr.myocrandroid.databinding.ActivityMainBinding;
import ru.myocr.myocrandroid.ocr.ReceiptScannerImpl;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_PICK_PHOTO = 0;
    public static final String TAG = "myOcr";

    private ActivityMainBinding binding;
    private Bitmap sourceImg;
    private Mat mat;
    private int idx = 0;
    private ReceiptScannerImpl scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonChoosePhoto.setOnClickListener(v -> loadPhoto());
        binding.buttonNext.setOnClickListener(v -> next());

        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        }
    }

    private void loadPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, CODE_PICK_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case CODE_PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(this.getContentResolver(), selectedImage);
                        sourceImg = bitmap;
                        binding.imageImg.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void next() {
        switch (idx) {
            case 0:
                scanner = new ReceiptScannerImpl();
                mat = scanner.loadImage(sourceImg);
                break;
            case 1:
                final Mat smallImage = scanner.downScaleImage(mat, 30);
                Log.d(TAG, "width = " + smallImage.width() +
                        " height = " + smallImage.height());
                binding.imageImg.setImageBitmap(matToBitmap(smallImage));
                break;
        }
        idx++;
        /*final Mat cannyImage = scanner.applyCannySquareEdgeDetectionOnImage(image);
        scanner.saveImage(cannyImage, "cannyReceipt.jpg");*/
    }

    private static Bitmap matToBitmap(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }
}
