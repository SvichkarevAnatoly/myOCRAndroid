package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import ru.myocr.api.Api;
import ru.myocr.api.ApiHelper;
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

        handleIncomingImage(getIntent());
        updateServerButtonText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    if (data != null && data.getData() != null) {
                        final Uri imageUri = data.getData();
                        Toast.makeText(MainActivity.this, imageUri.toString(), Toast.LENGTH_LONG).show();
                        startCropImageActivity(imageUri);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingImage(intent);
    }

    public void runCamScanner(View view) {
        Intent intent = new Intent("com.intsig.camscanner.NEW_DOC");
        startActivity(intent);
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        final Intent chooser = Intent.createChooser(intent, "Select Picture");
        startActivityForResult(chooser, PICK_IMAGE_REQUEST);
    }

    public void goToReceiptDirectly(View view) {
        String gigantProducts = getString(R.string.test_south_products);
        startActivityWithText(gigantProducts);

        String gigantPrices = getString(R.string.test_gigant_prices);
        startActivityWithText(gigantPrices);
    }

    public void changeServer(View view) {
        boolean isLocal = ApiHelper.getCurrentServerUrl().equals(Api.SERVER_URL_LOCAL);
        ApiHelper.setCurrentServerUrl(isLocal ? Api.SERVER_URL_AMAZON : Api.SERVER_URL_LOCAL);
        updateServerButtonText();
    }

    private void updateServerButtonText() {
        boolean isLocal = ApiHelper.getCurrentServerUrl().equals(Api.SERVER_URL_LOCAL);
        binding.buttonStartChangeServer.setText("S: " + (isLocal ? "LOCAL" : "AMAZON"));
    }

    private void startCropImageActivity(Uri imageUri) {
        final CropImage.ActivityBuilder activityBuilder = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMaxZoom(16)
                .setMultiTouchEnabled(true);
        final Intent intent = activityBuilder.getIntent(this);
        intent.setClass(this, CropActivity.class);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
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
