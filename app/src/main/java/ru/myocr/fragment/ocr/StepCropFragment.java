package ru.myocr.fragment.ocr;


import android.Manifest;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.activity.AddReceiptActivity;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.OcrRequest;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.FragmentStepCropBinding;
import ru.myocr.model.DbModel;
import ru.myocr.model.Shop;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Settings;
import ru.myocr.util.BitmapUtil;

import static ru.myocr.App.FILE_PROVIDER_AUTHORITY;

public class StepCropFragment extends Fragment implements CropImageView.OnCropImageCompleteListener {

    private static final int MAX_RAW_IMAGE_SIZE = 5 * 3 * 1024 * 1024; /*5 Mp*/

    private Uri mCropImageUri;
    private boolean isProductCropped = false;
    private FragmentStepCropBinding binding;

    private Bitmap receiptItem;

    private ProgressDialog progressDialog;
    private Rect cropProductRect;


    public StepCropFragment() {
        // Required empty public constructor
    }

    public static StepCropFragment newInstance(Uri photo) {
        StepCropFragment fragment = new StepCropFragment();
        Bundle args = new Bundle();
        args.putParcelable(AddReceiptActivity.ARG_OCR_PHOTO, photo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_crop, container, false);

        mCropImageUri = getArguments().getParcelable(AddReceiptActivity.ARG_OCR_PHOTO);

        if (savedInstanceState == null) {
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), mCropImageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                binding.cropImageView.setImageUriAsync(mCropImageUri);
            }
        }

        Toast.makeText(getActivity(), "Выделите продукты", Toast.LENGTH_SHORT).show();

        ApiHelper.makeApiRequest(Settings.getCityId(), ApiHelper::getShops,
                throwable -> {
                },
                this::onLoadShops, null);

        initShopSpinner();

        return binding.getRoot();
    }

    private void initShopSpinner() {
        List<Shop> shops = DbModel.getAll(Shop.class);
        List<String> names = new ArrayList<>();
        for (Shop shop : shops) {
            names.add(shop.getName());
        }
        binding.spinnerShop.setAdapter(
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names));
        binding.spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preference.setShopId(shops.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onLoadShops(List<Shop> shops) {
        Shop.putIfNotExist(shops);
        initShopSpinner();
    }

    public void onClickCrop() {
        if (!isProductCropped) {
            Toast.makeText(getActivity(), "Выделите цены", Toast.LENGTH_SHORT).show();
        }
        cropImage();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.cropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.cropImageView.setOnCropImageCompleteListener(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        if (!isProductCropped) {
            isProductCropped = true;
            receiptItem = BitmapUtil.compress(result.getBitmap(), MAX_RAW_IMAGE_SIZE);
            cropProductRect = result.getCropRect();
            Toast.makeText(getActivity(), "Изображение продуктов получено", Toast.LENGTH_LONG).show();
        } else {
            Bitmap prices = BitmapUtil.compress(result.getBitmap(), MAX_RAW_IMAGE_SIZE);

            final long city = Settings.getCityId();
            final long shop = Preference.getShopId();
            final OcrRequest ocrRequest = new OcrRequest(receiptItem, prices, city, shop);

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Загрузка...");
            }

            progressDialog.show();

            ApiHelper.makeApiRequest(ocrRequest, ApiHelper::ocr,
                    throwable -> {
                        progressDialog.hide();
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Ошибка").setMessage("Не удалось разпознать текст").show();
                    }, ocrReceiptResponse -> onOcrDone(result, ocrReceiptResponse), null);
        }
    }

    private void onOcrDone(CropImageView.CropResult result, OcrReceiptResponse ocrReceiptResponse) {
        progressDialog.hide();
        Uri imageUri = null;
        try {
            imageUri = createBitmapWithRects(cropProductRect, result.getCropRect());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((AddReceiptActivity) getActivity()).onCropFinish(ocrReceiptResponse, imageUri);
    }

    private void cropImage() {
        binding.cropImageView.getCroppedImageAsync();
    }

    private Uri createBitmapWithRects(Rect croppedProductsRect, Rect croppedPricesRect) throws IOException {
        Bitmap bitmapWithRects = drawRects(croppedProductsRect, croppedPricesRect);
        return saveBitmap(bitmapWithRects);
    }

    @NonNull
    private Bitmap drawRects(Rect croppedProductsRect, Rect croppedPricesRect) throws FileNotFoundException {
        Bitmap mutableBitmap = copyOcrBitmap();

        Paint paint = initPaint();
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawRect(croppedProductsRect, paint);
        canvas.drawRect(croppedPricesRect, paint);

        return mutableBitmap;
    }

    private Bitmap copyOcrBitmap() throws FileNotFoundException {
        InputStream imageStream = getActivity().getContentResolver().openInputStream(mCropImageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    @NonNull
    private Paint initPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Uri saveBitmap(Bitmap bitmap) throws IOException {
        File tempFile = BitmapUtil.createTempFile();
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.close();
        return FileProvider.getUriForFile(getActivity(), FILE_PROVIDER_AUTHORITY, tempFile);
    }
}
