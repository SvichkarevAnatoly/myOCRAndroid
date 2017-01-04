package ru.myocr.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityCameraBinding;
import ru.myocr.model.ocr.ReceiptScanner;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    public static final String TAG = "myOcr";

    private ActivityCameraBinding binding;

    private ReceiptScanner scanner = new ReceiptScanner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_camera);

        binding.cameraView.setCvCameraViewListener(this);
        binding.cameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        Mat filterImageWithDots = scanner.applyCannySquareEdgeDetectionOnImage(inputFrame,
                binding.seekBar.getProgress() / 100.0, binding.seekBar2.getProgress() / 100.0);
        scanner.findLines(inputFrame, filterImageWithDots, 0, 0);
        return inputFrame;
    }
}
