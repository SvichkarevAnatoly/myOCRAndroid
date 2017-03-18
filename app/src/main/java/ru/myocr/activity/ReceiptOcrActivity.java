package ru.myocr.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.ActivityReceiptOcrBinding;
import ru.myocr.fragment.OcrStepItemsFragment;
import ru.myocr.fragment.OcrStepReceiptDetailsFragment;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptData;

public class ReceiptOcrActivity extends AppCompatActivity {

    public static final String ARG_OCR_RESPONSE = "ARG_OCR_RESPONSE";

    private ActivityReceiptOcrBinding binding;

    private ReceiptData receiptData;
    private Receipt receipt;
    private OcrReceiptResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_ocr);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_ocr);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setTitle("Разбор чека");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        response = (OcrReceiptResponse) getIntent().getSerializableExtra(ARG_OCR_RESPONSE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, OcrStepItemsFragment.newInstance(response)).commit();

    }

    public void onReceiptDataSaved(ReceiptData receiptData) {
        this.receiptData = receiptData;
        showReceiptDetailFragment();
    }

    private void showReceiptDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, OcrStepReceiptDetailsFragment.newInstance(response)).commit();
    }

}
