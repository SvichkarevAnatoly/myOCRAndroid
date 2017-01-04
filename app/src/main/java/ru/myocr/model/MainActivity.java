package ru.myocr.model;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.io.IOException;
import java.util.Arrays;

import ru.myocr.model.databinding.ActivityMainBinding;
import ru.myocr.model.ocr.ImageHistory;
import ru.myocr.model.ocr.ReceiptScanner;

import static android.hardware.Camera.*;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    private static final int CODE_PICK_PHOTO = 0;
    public static final String TAG = "myOcr";

    private ActivityMainBinding binding;

    private ImageHistory imageHistory;
    private int idx;
    private ReceiptScanner scanner;
    private boolean isCamMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonChoosePhoto.setOnClickListener(v -> loadPhoto());
        binding.buttonNext.setOnClickListener(v -> doImageOperation(false));
        binding.buttonRepeat.setOnClickListener(v -> doImageOperation(true));
        binding.buttonMode.setOnClickListener(v -> {
            isCamMode = !isCamMode;
            if (isCamMode) {
                binding.frameCams.setVisibility(View.VISIBLE);
                binding.imageImg.setVisibility(View.GONE);
            } else {
                binding.frameCams.setVisibility(View.GONE);
                binding.imageImg.setVisibility(View.VISIBLE);
            }
        });

        binding.tutorial1ActivityJavaSurfaceView.setCvCameraViewListener(this);
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        }
        binding.tutorial1ActivityJavaSurfaceView.enableView();
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

    @Override
    public void onCameraViewStarted(int width, int height) {}

    @Override
    public void onCameraViewStopped() {}

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        ReceiptScanner scanner = new ReceiptScanner();
        if (!isCamMode) {
            return null;
        } else {
            Mat filterImageWithDots = scanner.applyCannySquareEdgeDetectionOnImage(inputFrame,
                    binding.seekBar.getProgress() / 100.0, binding.seekBar2.getProgress() / 100.0);
            MatOfPoint largestSquare = scanner.findLargestSquareOnCannyDetectedImage(filterImageWithDots);
            filterImageWithDots = scanner.drawLargestSquareOnCannyDetectedImage(filterImageWithDots, largestSquare);

            inputFrame.release();
            return filterImageWithDots;
        }
    }

    private void setCameraDisplayOrientation(Camera camera) {
        int cameraId = getCameraId();
        CameraInfo info = new CameraInfo();
        getCameraInfo(cameraId, info);


        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        camera.setDisplayOrientation((info.orientation - degrees + 360) % 360);
    }

    private int getCameraId() {
        final int numberOfCameras = getNumberOfCameras();

        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 0;
    }
}
