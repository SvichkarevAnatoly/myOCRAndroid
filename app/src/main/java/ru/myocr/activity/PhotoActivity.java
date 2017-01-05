package ru.myocr.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.util.Arrays;

import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityPhotoBinding;
import ru.myocr.newfeature.model.ocr.ImageHistory;
import ru.myocr.newfeature.model.ocr.ReceiptScanner;

public class PhotoActivity extends AppCompatActivity {

    public static final String TAG = "myOcr";
    private static final int CODE_PICK_PHOTO = 0;
    private ActivityPhotoBinding binding;

    private ImageHistory imageHistory;
    private int idx;
    private ReceiptScanner scanner;
    private int downScalePercent;
    private MatOfPoint contour;

    private static Bitmap matToBitmap(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo);
        binding.buttonChoosePhoto.setOnClickListener(v -> loadPhoto());
        binding.buttonNext.setOnClickListener(v -> doImageOperation(false));
        binding.buttonRepeat.setOnClickListener(v -> doImageOperation(true));
        binding.buttonBack.setOnClickListener(v -> backImageOperation());
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
                downScalePercent = binding.seekBar.getProgress();
                newImage = scanner.downScaleImage(curImage, downScalePercent);

                Log.d(TAG, "down scale new size: width = " + newImage.width() +
                        " height = " + newImage.height());
                break;
            case 1:
                newImage = scanner.applyCannySquareEdgeDetectionOnImage(curImage,
                        binding.seekBar.getProgress() / 100.0,
                        binding.seekBar2.getProgress() / 100.0);
                Log.d(TAG, "canny square edge detection");
                break;
            case 2:
                contour = scanner.findLargestSquareOnCannyDetectedImage(curImage);
                newImage = scanner.drawLargestSquareOnCannyDetectedImage(curImage, contour);
                Log.d(TAG, "find largestSquare: " + Arrays.toString(contour.toArray()));
                break;
            case 3:
                if (!scanner.canReduceTo4Dots(contour)) {
                    toastShow("Dots less than 4");
                    idx--;
                }
                contour = scanner.reduceTo4Dots(contour);

                Mat srcImage = imageHistory.getSource();
                newImage = scanner.transformPerspective(srcImage, downScalePercent, contour);
                Log.d(TAG, "transform perspective");
                break;
            case 4:
                newImage = scanner.smooth(curImage);
                Log.d(TAG, "smooth");
                break;
            default:
                newImage = curImage;
        }

        binding.imageImg.setImageBitmap(matToBitmap(newImage));
        imageHistory.change(newImage, isRepeated);
    }

    private void toastShow(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    private void backImageOperation() {
        idx--;
        imageHistory.removeLast();
        Mat prevImage = imageHistory.getLast();
        binding.imageImg.setImageBitmap(matToBitmap(prevImage));
    }
}
