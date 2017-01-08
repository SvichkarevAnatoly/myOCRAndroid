package ru.myocr.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.activity.adapter.ReceiptDataViewAdapter;
import ru.myocr.align.DataBaseFinder;
import ru.myocr.db.DbStub;
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
        binding.buttonScanPrices.setOnClickListener(v -> runOcrTextScanner());

        handleIncomingText(getIntent());
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
        // final List<String> matchesProducts = replaceMatchesInDB(products);
        // receiptData = new ReceiptDataImpl(matchesProducts);
        receiptData = new ReceiptDataImpl(products);
        updateProductsView();
    }

    private List<String> replaceMatchesInDB(List<String> ocrProducts) {
        final DbStub db = new DbStub();
        final List<String> allProducts = db.getAllProducts(this);

        final DataBaseFinder finder = new DataBaseFinder(allProducts);
        final List<String> matchProducts = finder.findAll(ocrProducts);

        return matchProducts;
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

    private void runOcrTextScanner() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.offline.ocr.english.image.to.text");
        startActivity(intent);
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
        EditText editText = new EditText(this);
        editText.setText(receiptData.getProducts().get(pos));
        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("Ok", (dialog, which) ->
                {
                    receiptData.getProducts().set(pos, editText.getText().toString());
                    updateProductsView();
                })
                .show();
    }

    @Override
    public void onClickPriceEdit(int pos) {

    }
}
