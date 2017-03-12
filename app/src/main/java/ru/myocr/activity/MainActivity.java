package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import ru.myocr.api.ApiHelper;
import ru.myocr.test.R;
import ru.myocr.test.databinding.ActivityMainBinding;
import ru.myocr.util.Preference;
import ru.myocr.util.Server;

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

        ApiHelper.makeApiRequest(null, ApiHelper::getAllCities,
                throwable -> {
                },
                this::onLoadCities, null);
    }

    private void onLoadCities(List<String> cities) {
        binding.spinnerCities.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities));
        binding.spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preference.setString(Preference.CITY, cities.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
        Preference.setString(Preference.SERVER_URL, getString(
                Server.isLocal() ? R.string.remote : R.string.localhost));
        updateServerButtonText();
    }

    private void updateServerButtonText() {
        binding.buttonStartChangeServer.setText("Server: " + (Server.isLocal() ? "LOCAL" : "AMAZON"));
    }

    private void startCropImageActivity(Uri imageUri) {
        final CropImage.ActivityBuilder activityBuilder = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMaxZoom(16)
                .setMinCropWindowSize(0, 0) // remove minimum restriction
                .setBorderCornerThickness(0) // remove border corners
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
