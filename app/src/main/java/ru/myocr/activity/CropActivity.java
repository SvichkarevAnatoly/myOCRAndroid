package ru.myocr.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageOptions;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myocr.api.Api;
import ru.myocr.api.OcrResponse;
import ru.myocr.model.R;
import ru.myocr.model.databinding.ActivityCropBinding;

public class CropActivity extends AppCompatActivity implements CropImageView.OnCropImageCompleteListener {

    private CropImageView mCropImageView;

    private Uri mCropImageUri;

    private CropImageOptions mOptions;

    private boolean isProductCropped = false;
    private ActivityCropBinding binding;

    private String products;
    private String prices;

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
            String title = mOptions.activityTitle != null && !mOptions.activityTitle.isEmpty()
                    ? mOptions.activityTitle
                    : getResources().getString(R.string.crop_image_activity_title);
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        binding.buttonOk.setOnClickListener(v -> onClickCrop());
        Toast.makeText(this, "Выделите продукты", Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResultCancel();
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (!isProductCropped) {
            isProductCropped = true;
            Bitmap productsBmp = result.getBitmap();
            requestOcr(productsBmp, true);
        } else {
            Bitmap pricesBmp = result.getBitmap();
            requestOcr(pricesBmp, false);
        }
    }

    private void requestOcr(Bitmap bmp, boolean isProducts) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Api.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final Api api = retrofit.create(Api.class);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] inputImage = stream.toByteArray();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), inputImage);
                MultipartBody.Part body = MultipartBody.Part.createFormData("imageUri", "testname", requestFile);

                final Call<OcrResponse> responseCall = api.ocr(body);
                try {
                    final Response<OcrResponse> response = responseCall.execute();
                    return response.body().ocrText;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String res) {
                super.onPostExecute(res);
                Toast.makeText(CropActivity.this, res, Toast.LENGTH_LONG).show();
                if (isProducts) {
                    products = res;
                } else {
                    prices = res;
                    goToReceiptDirectly();
                }
            }
        }.execute();
    }

    public void goToReceiptDirectly() {
        startActivityWithText(products);
        startActivityWithText(prices);
    }

    private void startActivityWithText(String text) {
        Intent intent = new Intent(this, ReceiptOcrActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    protected void cropImage() {
        mCropImageView.getCroppedImageAsync();
    }

    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}

