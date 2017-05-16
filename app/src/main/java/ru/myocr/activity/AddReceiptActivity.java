package ru.myocr.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.ActivityAddReceiptBinding;
import ru.myocr.fragment.OcrStepReceiptDetailsFragment;
import ru.myocr.fragment.ocr.OcrStepReceiptItemsFragment;
import ru.myocr.fragment.ocr.StepCropFragment;
import ru.myocr.model.ReceiptData;

public class AddReceiptActivity extends AppCompatActivity {

    public static final String ARG_OCR_RESPONSE = "ARG_OCR_RESPONSE";
    public static final String ARG_OCR_PHOTO = "ARG_OCR_PHOTO";
    public static final String ARG_OCR_ORIGIN_PHOTO = "ARG_OCR_ORIGIN_PHOTO";

    public static final String TAG_OCR_STEP_CROP = "StepCropFragment";
    public static final String TAG_OCR_STEP_ITEMS_FRAGMENT = "ReceiptItemsFragment";
    public static final String TAG_OCR_STEP_RECEIPT_DETAILS_FRAGMENT = "OcrStepReceiptDetailsFragment";

    private ActivityAddReceiptBinding binding;
    private int step;
    private Uri originPhotoUri;

    // from 1st step
    private OcrReceiptResponse response;
    private Uri cropItemsImageUri;
    // from 2nd step
    private ReceiptData receiptData;
    private boolean receiptDataSaved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_receipt);

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setTitle("Добавить чек");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        originPhotoUri = getIntent().getParcelableExtra(ARG_OCR_PHOTO);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, StepCropFragment.newInstance(originPhotoUri),
                        TAG_OCR_STEP_CROP).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_receipt_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_ok:
                if (0 == step) {
                    StepCropFragment stepCropFragment = (StepCropFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_OCR_STEP_CROP);
                    stepCropFragment.onClickCrop();
                } else if (1 == step) {
                    OcrStepReceiptItemsFragment ocrStepReceiptItemsFragment = (OcrStepReceiptItemsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_OCR_STEP_ITEMS_FRAGMENT);
                    ocrStepReceiptItemsFragment.onClickNext();
                } else {
                    OcrStepReceiptDetailsFragment ocrStepReceiptDetailsFragment = (OcrStepReceiptDetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_OCR_STEP_RECEIPT_DETAILS_FRAGMENT);
                    ocrStepReceiptDetailsFragment.onClickSave();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCropFinish(OcrReceiptResponse response, Uri cropItemsImageUri) {
        this.response = response;
        this.cropItemsImageUri = cropItemsImageUri;
        step++;

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container, OcrStepReceiptItemsFragment.newInstance(response, cropItemsImageUri),
                        TAG_OCR_STEP_ITEMS_FRAGMENT)
                .commit();
    }

    public void onReceiptDataSaved(ReceiptData receiptData) {
        this.receiptData = receiptData;
        receiptDataSaved = true;
        step++;
        showReceiptDetailFragment();
    }

    private void showReceiptDetailFragment() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.container, OcrStepReceiptDetailsFragment.newInstance(receiptData,
                        cropItemsImageUri), TAG_OCR_STEP_RECEIPT_DETAILS_FRAGMENT)
                .commit();
    }
}
