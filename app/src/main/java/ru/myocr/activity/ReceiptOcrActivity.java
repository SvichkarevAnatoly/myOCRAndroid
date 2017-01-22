package ru.myocr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.myocr.activity.adapter.ReceiptDataViewAdapter;
import ru.myocr.api.Api;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.FindAllRequest;
import ru.myocr.api.FindAllResponse;
import ru.myocr.model.OcrParser;
import ru.myocr.model.R;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptDataImpl;
import ru.myocr.model.databinding.ActivityReceiptOcrBinding;
public class ReceiptOcrActivity extends AppCompatActivity implements ReceiptDataViewAdapter.OnItemClickListener {

    private ActivityReceiptOcrBinding binding;

    private boolean hasProducts;
    private ReceiptData receiptData;
    private OcrParser parser;

    private ReceiptDataViewAdapter receiptViewAdapter;
    private List<Pair<String, String>> productPricePairs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_ocr);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_ocr);
        binding.buttonFindAll.setOnClickListener(v -> findInServer());

        handleIncomingText(getIntent());

    }

    private void findInServer() {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiHelper.getCurrentServerUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final Api api = retrofit.create(Api.class);
                final FindAllRequest request = new FindAllRequest(receiptData.getProducts());
                final Call<FindAllResponse> responseCall = api.findAll(request);
                try {
                    final Response<FindAllResponse> response = responseCall.execute();
                    return response.body().match;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<String> res) {
                super.onPostExecute(res);
                //Toast.makeText(ReceiptOcrActivity.this, "" + res, Toast.LENGTH_LONG).show();
                receiptData.setProducts(res);
                updateProductsView();
            }
        }.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingText(intent);
    }

    private void handleIncomingText(Intent intent) {
        if (intent != null) {
            handleText(intent);
        }
    }

    void handleText(Intent intent) {
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
        updateProductsView();
    }

    private void updateProductsView() {
        productPricePairs.clear();
        productPricePairs.addAll(receiptData.getProductsPricesPairs());
        if (receiptViewAdapter == null) {
            receiptViewAdapter = new ReceiptDataViewAdapter(this, productPricePairs, this);
            binding.listReceiptData.setAdapter(receiptViewAdapter);
        } else {
            receiptViewAdapter.notifyDataSetChanged();
        }
        receiptViewAdapter.setProductSize(receiptData.getProductSize());
        receiptViewAdapter.setPriceSize(receiptData.getPriceSize());
    }

    @Override
    public void onClickProductRemove(int pos) {
        receiptData.removeProduct(pos);
        updateProductsView();
    }

    @Override
    public void onClickPriceRemove(int pos) {
        receiptData.removePrice(pos);
        updateProductsView();
    }

    @Override
    public void onClickProductDown(int pos) {
        receiptData.shiftProductDown(pos);
        updateProductsView();
    }

    @Override
    public void onClickProductUp(int pos) {
        receiptData.shiftProductUp(pos);
        updateProductsView();
    }

    @Override
    public void onClickPriceDown(int pos) {
        receiptData.shiftPriceDown(pos);
        updateProductsView();
    }

    @Override
    public void onClickProductEdit(int pos) {
        showEditTextDialog(receiptData.getProducts().get(pos), text -> {
            receiptData.getProducts().set(pos, text);
            updateProductsView();
        });
    }

    @Override
    public void onClickPriceEdit(int pos) {
        showEditTextDialog(receiptData.getPrices().get(pos), text -> {
            receiptData.getPrices().set(pos, text);
            updateProductsView();
        });
    }


    public void showEditTextDialog(String text, OnEditTextListener callback) {
        EditText editText = new EditText(this);
        editText.setText(text);
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("Ok",
                        (dialog, which) -> callback.onEdit(editText.getText().toString()))
                .show();
    }

    private interface OnEditTextListener {
        void onEdit(String text);
    }
}
