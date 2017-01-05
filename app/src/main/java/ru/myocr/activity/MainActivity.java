package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "myOcr";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.buttonRunCamScanner.setOnClickListener(v -> runCamScanner());
        binding.buttonRunOcrTextScanner.setOnClickListener(v -> runOcrTextScanner());
        binding.buttonStartIntent.setOnClickListener(v -> goToReceiptDirectly());
    }

    private void runCamScanner() {
        Intent intent = new Intent("com.intsig.camscanner.NEW_DOC");
        startActivity(intent);
    }

    private void runOcrTextScanner() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.offline.ocr.english.image.to.text");
        startActivity(intent);
    }

    private void goToReceiptDirectly() {
        String gigantProducts = getString(R.string.test_gigant_products);
        startActivityWithText(gigantProducts);

        String gigantPrices = getString(R.string.test_gigant_prices);
        startActivityWithText(gigantPrices);
    }

    private void startActivityWithText(String text) {
        Intent intent = new Intent(this, ReceiptOcrActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
