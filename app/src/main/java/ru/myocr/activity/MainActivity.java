package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityMainBinding;
import ru.myocr.newfeature.activity.CameraActivity;
import ru.myocr.newfeature.activity.PhotoActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "myOcr";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonRunCamScanner.setOnClickListener(v -> runCamScanner());
        binding.buttonPhoto.setOnClickListener(v -> startPhoto());
        binding.buttonCamera.setOnClickListener(v -> startCamera());

        initOpenCV();
    }

    private void runCamScanner() {
        Intent intent = new Intent("com.intsig.camscanner.NEW_DOC");
        startActivity(intent);
    }

    private void startPhoto() {
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    private void startCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void initOpenCV() {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        }
    }
}
