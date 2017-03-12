package ru.myocr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.activity.adapter.ReceiptDataViewAdapter;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.SavePriceRequest;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.api.ocr.ParsedPrice;
import ru.myocr.api.ocr.ReceiptItemMatches;
import ru.myocr.databinding.ActivityReceiptOcrBinding;
import ru.myocr.databinding.ReceiptItemEditDialogBinding;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptItemPriceViewItem;
import ru.myocr.preference.Preference;
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

    /*public void findInServer(View view) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Server.getUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final Api api = retrofit.create(Api.class);
                final FindRequest request = new FindRequest(receiptData.getReceiptItemMatches());
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
    }*/

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
            receiptData = new ReceiptData(response);
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
        receiptViewAdapter.setProductSize(receiptData.size());
        receiptViewAdapter.setPriceSize(receiptData.size());
    }

    @Override
    public void onClickReceiptItemRemove(int pos) {
        receiptData.removeProduct(pos);
        updateProductsView();
    }

    @Override
    public void onClickPriceRemove(int pos) {
        receiptData.removePrice(pos);
        updateProductsView();
    }

    @Override
    public void onClickReceiptItemDown(int pos) {
        receiptData.shiftProductDown(pos);
        updateProductsView();
    }

    @Override
    public void onClickReceiptItemUp(int pos) {
        receiptData.shiftProductUp(pos);
        updateProductsView();
    }

    @Override
    public void onClickPriceDown(int pos) {
        receiptData.shiftPriceDown(pos);
        updateProductsView();
    }

    @Override
    public void onClickItemEdit(int pos) {
        showEditReceiptItemDialog(receiptData.getReceiptItemPriceViewItem(pos),
                (receiptItem, price) -> {
                    receiptData.getReceiptItemPriceViewItem(pos).setReceiptItem(receiptItem);
                    receiptData.getReceiptItemPriceViewItem(pos).setPrice(price);
                    updateProductsView();
                });
    }

    public void showEditReceiptItemDialog(ReceiptItemPriceViewItem item, OnEditTextListener callback) {

        final ReceiptItemEditDialogBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.receipt_item_edit_dialog, null, false);

        binding.receiptItemEditText.setText(item.getReceiptItem());
        binding.priceEditText.setText(item.getPrice());
        binding.receiptItemMatches.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, item.getMatches()
        ));
        final boolean[] isSelected = {false};
        binding.receiptItemMatches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isSelected[0]) {
                    isSelected[0] = true;
                    return;
                }
                final String newText = item.getMatches().get(position);
                binding.receiptItemEditText.setText(newText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        new AlertDialog.Builder(this)
                .setView(binding.getRoot())
                .setPositiveButton("Ok",
                        (dialog, which) -> callback.onEdit(
                                binding.receiptItemEditText.getText().toString(),
                                binding.priceEditText.getText().toString()))
                .show();
    }

    private interface OnEditTextListener {
        void onEdit(String receiptItem, String price);
    }
}
