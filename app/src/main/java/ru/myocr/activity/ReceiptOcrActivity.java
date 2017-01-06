package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.myocr.model.OcrParser;
import ru.myocr.model.R;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptDataImpl;
import ru.myocr.model.databinding.ActivityReceiptOcrBinding;
import ru.myocr.model.databinding.ReceiptDataItemBinding;

public class ReceiptOcrActivity extends AppCompatActivity {

    private ActivityReceiptOcrBinding binding;

    private boolean hasProducts;
    private ReceiptData receiptData;
    private OcrParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_ocr);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_ocr);
        binding.buttonScanPrices.setOnClickListener(v -> runOcrTextScanner());

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
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

            // if (Intent.ACTION_SEND.equals(action) && type != null) {
            //     if ("text/plain".equals(type)) {
                    // Handle text being sent
                    handleSendText(intent);
            // }
            // }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if (!hasProducts) {
                parser = new OcrParser(sharedText);
                final List<String> products = parser.parseProductList();
                receiptData = new ReceiptDataImpl(products);
                hasProducts = true;
            } else {
                parser.setPricesText(sharedText);
                final List<String> products = receiptData.getProducts();
                final List<String> prices = parser.parsePriceList();
                receiptData = new ReceiptDataImpl(products, prices);
            }
        }
        updateReceiptDataView();
    }

    private void updateReceiptDataView() {
        final ArrayAdapter<Pair<String, String>> adapter =
                new ArrayAdapter<Pair<String, String>>(this, 0, receiptData.getProductsPricesPairs()) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final ReceiptDataItemBinding binding =
                                DataBindingUtil.inflate(getLayoutInflater(), R.layout.receipt_data_item, parent, false);
                        final Pair<String, String> item = getItem(position);
                        binding.textProductPricePair.setText(item.first + item.second);
                        return binding.getRoot();
                    }
                };
        binding.listReceiptData.setAdapter(adapter);
    }
}
