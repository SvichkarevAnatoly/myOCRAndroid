package ru.myocr.model;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Camera;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.Arrays;

import ru.myocr.model.databinding.ActivityMainBinding;
import ru.myocr.model.ocr.ReceiptScanner;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener{

    private static final int CODE_PICK_PHOTO = 0;
    public static final String TAG = "myOcr";

    private ActivityMainBinding binding;
    private Bitmap sourceImg;
    private Mat mat;
    private Mat matPrev;
    private int idx = 0;
    private ReceiptScanner scanner;
    private boolean isCamMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonChoosePhoto.setOnClickListener(v -> loadPhoto());
        binding.buttonNext.setOnClickListener(v -> {
            idx++;
            doOperation(false);
        });
        binding.buttonRepeat.setOnClickListener(v -> {
            doOperation(true);
        });

        binding.buttonMode.setOnClickListener(v -> {
            isCamMode = !isCamMode;
            if (isCamMode){
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
        //binding.tutorial1ActivityJavaSurfaceView.ori/
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
                        scanner = new ReceiptScanner();
                        mat = scanner.loadImage(sourceImg);
                        idx = -1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void doOperation(boolean isRepeated) {
        Log.d(TAG, "Operation: " + idx + " isRepeated = " + isRepeated);
        if (isRepeated){
            mat = matPrev;
        } else {
            matPrev = mat.clone();
        }
        switch (idx) {
            case 0:
                mat = scanner.downScaleImage(mat, binding.seekBar.getProgress());
                Log.d(TAG, "down scale new size: width = " + mat.width() +
                        " height = " + mat.height());
                binding.imageImg.setImageBitmap(matToBitmap(mat));
                break;
            case 1:
                mat = scanner.applyCannySquareEdgeDetectionOnImage(this.mat,
                        binding.seekBar.getProgress() / 100.0, binding.seekBar2.getProgress() / 100.0);
                Log.d(TAG, "canny square edge detection");
                binding.imageImg.setImageBitmap(matToBitmap(mat));
                break;
            case 2:
                MatOfPoint largestSquare = scanner.findLargestSquareOnCannyDetectedImage(this.mat);
                Log.d(TAG, "find largestSquare: " + Arrays.toString(largestSquare.toArray()));
                scanner.drawLargestSquareOnCannyDetectedImage(this.mat, largestSquare);
                binding.imageImg.setImageBitmap(matToBitmap(this.mat));
                break;
        }
    }

    private static Bitmap matToBitmap(Mat mat) {
        Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp);
        return bmp;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        ReceiptScanner scanner = new ReceiptScanner();
        if (!isCamMode){
            return null;
        } else {
            Mat matEdges = scanner.applyCannySquareEdgeDetectionOnImage(inputFrame,
                    binding.seekBar.getProgress() / 100.0, binding.seekBar2.getProgress() / 100.0);
            MatOfPoint largestSquare = scanner.findLargestSquareOnCannyDetectedImage(matEdges);
            scanner.drawLargestSquareOnCannyDetectedImage(matEdges, largestSquare);

            inputFrame.release();
            return matEdges;
        }
    }

    private void setCameraDisplayOrientation(Camera camera) {
        int cameraId = getCameraId();
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);


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

        camera.setsetDisplayOrientation((info.orientation - degrees + 360) % 360);
}
