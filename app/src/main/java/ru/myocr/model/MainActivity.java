package ru.myocr.model;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.util.Arrays;

import ru.myocr.model.databinding.ActivityMainBinding;
import ru.myocr.model.ocr.ImageHistory;
import ru.myocr.model.ocr.ReceiptScanner;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_PICK_PHOTO = 0;
    public static final String TAG = "myOcr";

    private ActivityMainBinding binding;

    private ImageHistory imageHistory;
    private int idx;
    private ReceiptScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonChoosePhoto.setOnClickListener(v -> loadPhoto());
        binding.buttonNext.setOnClickListener(v -> doImageOperation(false));
        binding.buttonRepeat.setOnClickListener(v -> doImageOperation(true));
        binding.buttonBack.setOnClickListener(v -> backImageOperation());

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
                        binding.imageImg.setImageBitmap(bitmap);
                        scanner = new ReceiptScanner();
                        final Mat image = scanner.loadImage(bitmap);
                        imageHistory = new ImageHistory(image);
                        idx = -1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void doImageOperation(boolean isRepeated) {
        if (!isRepeated) {
            idx++;
        }
        Log.d(TAG, "Operation: " + idx + " isRepeated = " + isRepeated);

        final Mat curImage = imageHistory.getLast(isRepeated);

        final Mat newImage;
        switch (idx) {
            case 0:
                newImage = scanner.downScaleImage(curImage, binding.seekBar.getProgress());

                Log.d(TAG, "down scale new size: width = " + newImage.width() +
                        " height = " + newImage.height());
                binding.imageImg.setImageBitmap(matToBitmap(newImage));
                break;
            case 1:
                newImage = scanner.applyCannySquareEdgeDetectionOnImage(curImage,
                        binding.seekBar.getProgress() / 100.0,
                        binding.seekBar2.getProgress() / 100.0);
                Log.d(TAG, "canny square edge detection");
                binding.imageImg.setImageBitmap(matToBitmap(newImage));
                break;
            case 2:
                final MatOfPoint largestSquare = scanner.findLargestSquareOnCannyDetectedImage(curImage);
                Log.d(TAG, "find largestSquare: " + Arrays.toString(largestSquare.toArray()));
                newImage = scanner.drawLargestSquareOnCannyDetectedImage(curImage, largestSquare);
                binding.imageImg.setImageBitmap(matToBitmap(newImage));
                break;
            default:
                newImage = curImage;
        }

        imageHistory.change(newImage, isRepeated);
    }

    private void backImageOperation() {
        idx--;
        imageHistory.removeLast();
        Mat prevImage = imageHistory.getLast();
        binding.imageImg.setImageBitmap(matToBitmap(prevImage));
    }

    private static Bitmap matToBitmap(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }
}
