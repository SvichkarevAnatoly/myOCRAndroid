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
import java.io.File;
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

public class CropActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {

    /**
     * The crop image view library widget used in the activity
     */
    private CropImageView mCropImageView;

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private Uri mCropImageUri;

    /**
     * the options that were set for the crop image
     */
    private CropImageOptions mOptions;

    private boolean isProductCropped = false;
    private ActivityCropBinding binding;

    private Uri croppedProductImage;
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
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
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
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            if (mOptions.initialCropWindowRectangle != null) {
                mCropImageView.setCropRect(mOptions.initialCropWindowRectangle);
            }
            if (mOptions.initialRotation > -1) {
                mCropImageView.setRotatedDegrees(mOptions.initialRotation);
            }
        } else {
            setResult(null, error, 1);
        }
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
    //region: Private methods

    /**
     * Execute crop image and save the result tou output uri.
     */
    protected void cropImage() {
        if (mOptions.noOutputImage) {
            setResult(null, null, 1);
        } else {
            mCropImageView.getCroppedImageAsync();
        }
    }

    /**
     * Get Android uri to save the cropped image into.<br>
     * Use the given in options or create a temp file.
     */
    protected Uri getOutputUri() {
        Uri outputUri = mOptions.outputUri;
        if (outputUri.equals(Uri.EMPTY)) {
            try {
                String ext = mOptions.outputCompressFormat == Bitmap.CompressFormat.JPEG ? ".jpg" :
                        mOptions.outputCompressFormat == Bitmap.CompressFormat.PNG ? ".png" : ".webp";
                outputUri = Uri.fromFile(File.createTempFile("cropped" + (isProductCropped ? "Product" : "Price")
                        , ext, getCacheDir()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temp file for output image", e);
            }
        }
        return outputUri;
    }

    /**
     * Result with cropped image data or error if failed.
     */
    protected void setResult(Uri uri, Exception error, int sampleSize) {
        int resultCode = error == null ? RESULT_OK : CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE;
        setResult(resultCode, getResultIntent(uri, error, sampleSize));
        finish();
    }

    /**
     * Cancel of cropping activity.
     */
    protected void setResultCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Get intent instance to be used for the result of this activity.
     */
    protected Intent getResultIntent(Uri uriProduct, Exception error, int sampleSize) {
        CropImage.ActivityResult result = new CropImage.ActivityResult(null,
                uriProduct,
                error,
                mCropImageView.getCropPoints(),
                mCropImageView.getCropRect(),
                mCropImageView.getRotatedDegrees(),
                sampleSize);
        Intent intent = new Intent();
        intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, result);
        return intent;
    }

    //endregion
}

