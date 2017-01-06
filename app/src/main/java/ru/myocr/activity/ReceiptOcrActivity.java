package ru.myocr.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.myocr.model.OcrParser;
import ru.myocr.model.R;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptDataImpl;
import ru.myocr.model.databinding.ActivityReceiptOcrBinding;
import ru.myocr.model.databinding.ReceiptPriceItemBinding;
import ru.myocr.model.databinding.ReceiptProductItemBinding;

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
        if (intent != null) {
            handleSendText(intent);
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if (!hasProducts) {
                updateProducts(sharedText);
                hasProducts = true;
            } else {
                updatePrices(sharedText);
            }
        }
    }

    private void updateProducts(String sharedText) {
        parser = new OcrParser(sharedText);
        final List<String> products = parser.parseProductList();
        receiptData = new ReceiptDataImpl(products);
        updateProductsView();
    }

    private void updatePrices(String sharedText) {
        parser.setPricesText(sharedText);
        final List<String> products = receiptData.getProducts();
        final List<String> prices = parser.parsePriceList();
        receiptData = new ReceiptDataImpl(products, prices);
        updatePricesView();
    }

    private void updateProductsView() {
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, 0, receiptData.getProducts()) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final ReceiptProductItemBinding binding =
                                DataBindingUtil.inflate(getLayoutInflater(), R.layout.receipt_product_item, parent, false);
                        final String item = getItem(position);
                        binding.textProduct.setText(item);
                        return binding.getRoot();
                    }
                };
        binding.listReceiptProducts.setAdapter(adapter);
    }

    private void updatePricesView() {
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, 0, receiptData.getPrices()) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final ReceiptPriceItemBinding binding =
                                DataBindingUtil.inflate(getLayoutInflater(), R.layout.receipt_price_item, parent, false);
                        final String item = getItem(position);
                        binding.textPrice.setText(item);
                        return binding.getRoot();
                    }
                };
        binding.listReceiptPrices.setAdapter(adapter);
    }
}
