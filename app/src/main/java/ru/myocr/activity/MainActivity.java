package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "myOcr";
    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonRunCamScanner.setOnClickListener(v -> runCamScanner());
        binding.buttonSelectImage.setOnClickListener(v -> selectImage());
        binding.buttonStartIntent.setOnClickListener(v -> goToReceiptDirectly());

        handleIncomingImage(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    if (data != null && data.getData() != null) {
                        final Uri imageUri = data.getData();
                        startCropImageActivity(imageUri);
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    final CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    final Uri imageUri = result.getUri();
                    binding.imagePreview.setImageURI(imageUri);
                    Toast.makeText(this,
                            "Cropping successful, Sample: " + result.getSampleSize(),
                            Toast.LENGTH_LONG)
                            .show();
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingImage(intent);
    }

    private void runCamScanner() {
        Intent intent = new Intent("com.intsig.camscanner.NEW_DOC");
        startActivity(intent);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        final Intent chooser = Intent.createChooser(intent, "Select Picture");
        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void goToReceiptDirectly() {
        String gigantProducts = getString(R.string.test_south_products);
        startActivityWithText(gigantProducts);

        String gigantPrices = getString(R.string.test_gigant_prices);
        startActivityWithText(gigantPrices);
    }

    private void startActivityWithText(String text) {
        Intent intent = new Intent(this, ReceiptOcrActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    private void handleIncomingImage(Intent intent) {
        if (intent != null) {
            final String type = intent.getType();
            final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (type != null && type.startsWith("image/") && imageUri != null) {
                startCropImageActivity(imageUri);
            }
        }
    }
}
