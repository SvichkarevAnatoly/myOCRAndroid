package ru.myocr.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageOptions;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ru.myocr.R;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.OcrRequest;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.ActivityCropBinding;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Settings;
import ru.myocr.util.BitmapUtil;

import static ru.myocr.App.FILE_PROVIDER_AUTHORITY;

public class CropActivity extends AppCompatActivity implements CropImageView.OnCropImageCompleteListener {

    private CropImageView mCropImageView;

    private Uri mCropImageUri;

    private CropImageOptions mOptions;

    private boolean isProductCropped = false;
    private ActivityCropBinding binding;

    private Bitmap receiptItem;

    private ProgressDialog progressDialog;
    private Rect cropProductRect;

    private static Rect unionRect(Rect productRect, Rect pricesRect) {
        return new Rect(productRect.left, Math.min(productRect.top, pricesRect.top),
                pricesRect.right, Math.max(productRect.bottom, pricesRect.bottom));
    }

    @Override
    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop);

        mCropImageView = (CropImageView) findViewById(R.id.cropImageView);

        Intent intent = getIntent();
        mCropImageUri = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_SOURCE);
        mOptions = intent.getParcelableExtra(CropImage.CROP_IMAGE_EXTRA_OPTIONS);

        if (savedInstanceState == null) {
            if (CropImage.isReadExternalStoragePermissionsRequired(this, mCropImageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                mCropImageView.setImageUriAsync(mCropImageUri);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = "Распознать чек";
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.buttonOk.setOnClickListener(v -> onClickCrop());
        Toast.makeText(this, "Выделите продукты", Toast.LENGTH_SHORT).show();

        ApiHelper.makeApiRequest(Settings.getString(Settings.CITY), ApiHelper::getShops,
                throwable -> {
                },
                this::onLoadShops, null);
    }

    private void onLoadShops(List<String> shops) {
        binding.spinnerShop.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shops));
        binding.spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String shop = shops.get(position);
                Preference.setString(Preference.SHOP, shop);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onClickCrop() {
        if (!isProductCropped) {
            Toast.makeText(this, "Выделите цены", Toast.LENGTH_SHORT).show();
        }
        cropImage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnCropImageCompleteListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (!isProductCropped) {
            isProductCropped = true;
            receiptItem = result.getBitmap();
            cropProductRect = result.getCropRect();
            Toast.makeText(CropActivity.this, "Изображение продуктов получено", Toast.LENGTH_LONG).show();
        } else {
            Bitmap prices = result.getBitmap();

            final String city = Settings.getString(Settings.CITY);
            final String shop = Preference.getString(Preference.SHOP);
            final OcrRequest ocrRequest = new OcrRequest(receiptItem, prices, city, shop);

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Загрузка...");
            }

            progressDialog.show();

            ApiHelper.makeApiRequest(ocrRequest, ApiHelper::ocr,
                    throwable -> {
                        progressDialog.hide();
                        new AlertDialog.Builder(this)
                                .setTitle("Ошибка").setMessage("Не удалось разпознать текст").show();
                    }, ocrReceiptResponse -> {
                        progressDialog.hide();
                        Uri itemsImageUri = null;
                        try {
                            itemsImageUri = cropItemsImage(cropProductRect, result.getCropRect());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        startActivity(ocrReceiptResponse, itemsImageUri);
                    }, null);
        }
    }

    private Uri cropItemsImage(Rect cropProductRect, Rect cropPricesRect) throws IOException {
        InputStream imageStream = getContentResolver().openInputStream(mCropImageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

        Rect unionRect = unionRect(cropProductRect, cropPricesRect);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(unionRect, paint);

        File tempFile = BitmapUtil.createTempFile();
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();

        return FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, tempFile);
    }

    private void startActivity(OcrReceiptResponse response, Uri cropItemsImageUri) {
        Intent intent = new Intent(this, ReceiptOcrActivity.class);
        intent.putExtra(ReceiptOcrActivity.ARG_OCR_RESPONSE, response);
        intent.putExtra(ReceiptOcrActivity.ARG_OCR_PHOTO, cropItemsImageUri);
        intent.putExtra(ReceiptOcrActivity.ARG_OCR_ORIGIN_PHOTO, mCropImageUri);
        startActivity(intent);
        finish();
    }

    protected void cropImage() {
        mCropImageView.getCroppedImageAsync();
    }

}

