package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityReceiptOcrBinding;

public class ReceiptOcrActivity extends AppCompatActivity {

    private ActivityReceiptOcrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_ocr);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_ocr);
        binding.buttonScanPrices.setOnClickListener(v -> runOcrTextScanner());

        binding.editProducts.setHorizontallyScrolling(true);
        binding.editPrices.setHorizontallyScrolling(true);

        handleIncomingIntent(getIntent());
    }

    private void runOcrTextScanner() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.offline.ocr.english.image.to.text");
        startActivity(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        // Get intent, action and MIME type
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    // Handle text being sent
                    handleSendText(intent);
                }
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if (binding.editProducts.getText().toString().isEmpty()) {
                binding.editProducts.setText(sharedText);
            } else {
                binding.editPrices.setText(sharedText);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }
}
