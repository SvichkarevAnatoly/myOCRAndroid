package ru.myocr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import ru.myocr.api.FindRequest;
import ru.myocr.api.FindResponse;
import ru.myocr.api.SavePriceRequest;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Server;
import ru.myocr.test.R;
import ru.myocr.test.ReceiptData;
import ru.myocr.test.ReceiptDataImpl;
import ru.myocr.test.databinding.ActivityReceiptOcrBinding;
import ru.myocr.util.PriceUtil;

public class ReceiptOcrActivity extends AppCompatActivity implements ReceiptDataViewAdapter.OnItemClickListener {

    public static final String ARG_OCR_RESPONSE = "ARG_OCR_RESPONSE";

    private ActivityReceiptOcrBinding binding;

    private ReceiptData receiptData;

    private ReceiptDataViewAdapter receiptViewAdapter;
    private List<Pair<String, String>> productPricePairs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_ocr);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_receipt_ocr);

        handleIncomingText(getIntent());
    }

    public void findInServer(View view) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Server.getUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final Api api = retrofit.create(Api.class);
                final FindRequest request = new FindRequest(receiptData.getProducts());
                final Call<FindResponse> responseCall = api.find(request);
                try {
                    final Response<FindResponse> response = responseCall.execute();
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

    public void addToDb(View view) {
        final SavePriceRequest savePriceRequest = convert(productPricePairs);
        ApiHelper.makeApiRequest(savePriceRequest, ApiHelper::save,
                throwable -> Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show(),
                integer -> Toast.makeText(this, "Успешно сохранено " + integer + " записей",
                        Toast.LENGTH_SHORT).show(), null);
    }

    private SavePriceRequest convert(List<Pair<String, String>> productPricePairs) {
        final String city = Preference.getString(Preference.CITY);
        final String shop = Preference.getString(Preference.SHOP);

        final ArrayList<SavePriceRequest.ReceiptPriceItem> items = new ArrayList<>();
        for (Pair<String, String> pair : productPricePairs) {
            if (pair.first == null || pair.second == null) {
                break;
            }
            final int price = Integer.parseInt(pair.second.replace(".", ""));
            items.add(new SavePriceRequest.ReceiptPriceItem(pair.first, price));
        }

        return new SavePriceRequest(city, shop, items);
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
        OcrReceiptResponse response = (OcrReceiptResponse) intent.getSerializableExtra(ARG_OCR_RESPONSE);
        if (response != null) {
            List<String> receiptItems = getReceiptItems(response.getItemMatches());
            List<String> prices = getPrices(response.getPrices());

            receiptData = new ReceiptDataImpl(receiptItems, prices);
            updateProductsView();
        }
    }

    private List<String> getReceiptItems(List<ReceiptItemMatches> itemMatches) {
        final ArrayList<String> receiptItems = new ArrayList<>(itemMatches.size());
        for (ReceiptItemMatches itemMatch : itemMatches) {
            receiptItems.add(itemMatch.getMatches().get(0).getMatch());
        }
        return receiptItems;
    }

    private List<String> getPrices(List<ParsedPrice> parsedPrices) {
        final ArrayList<String> prices = new ArrayList<>(parsedPrices.size());
        for (ParsedPrice price : parsedPrices) {
            final String priceValue = PriceUtil.getValue(price);
            prices.add(priceValue);
        }
        return prices;
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
