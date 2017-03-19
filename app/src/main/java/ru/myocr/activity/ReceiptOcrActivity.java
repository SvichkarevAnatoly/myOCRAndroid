package ru.myocr.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.ActivityReceiptOcrBinding;
import ru.myocr.fragment.OcrStepItemsFragment;
import ru.myocr.fragment.OcrStepReceiptDetailsFragment;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptData;

public class ReceiptOcrActivity extends AppCompatActivity {

    public static final String ARG_OCR_RESPONSE = "ARG_OCR_RESPONSE";
    public static final String TAG_OCR_STEP_ITEMS_FRAGMENT = "OcrStepItemsFragment";
    public static final String TAG_OCR_STEP_RECEIPT_DETAILS_FRAGMENT = "OcrStepReceiptDetailsFragment";

    private ActivityReceiptOcrBinding binding;

    private ReceiptData receiptData;
    private Receipt receipt;
    private OcrReceiptResponse response;
    private boolean receiptDataSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_ocr);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_ocr);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setTitle("Добавить чек");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        response = (OcrReceiptResponse) getIntent().getSerializableExtra(ARG_OCR_RESPONSE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, OcrStepItemsFragment.newInstance(response), TAG_OCR_STEP_ITEMS_FRAGMENT).commit();

        binding.floatingMenu.setOnClickListener(v -> {
            if (!receiptDataSaved) {
                OcrStepItemsFragment fragment = (OcrStepItemsFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_OCR_STEP_ITEMS_FRAGMENT);
                fragment.addToDb();
            } else {
                OcrStepReceiptDetailsFragment fragment = (OcrStepReceiptDetailsFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_OCR_STEP_RECEIPT_DETAILS_FRAGMENT);
                fragment.onClickSave();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onReceiptDataSaved(ReceiptData receiptData) {
        this.receiptData = receiptData;
        receiptDataSaved = true;
        showReceiptDetailFragment();
    }

    private void showReceiptDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, OcrStepReceiptDetailsFragment.newInstance(receiptData),
                        TAG_OCR_STEP_RECEIPT_DETAILS_FRAGMENT).commit();

        binding.floatingMenu.setImageResource(R.drawable.ic_check_white_18dp);
    }

}
