package ru.myocr.fragment.ocr;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.activity.AddReceiptActivity;
import ru.myocr.activity.adapter.ReceiptDataViewAdapter;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.ReceiptItemsInShopRequest;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.FragmentReceiptOcrBinding;
import ru.myocr.databinding.ReceiptItemEditDialogBinding;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptItemPriceViewItem;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Settings;
import ru.myocr.util.collection.ArrayStack;
import ru.myocr.util.collection.Stack;

public class ReceiptItemsFragment extends Fragment implements ReceiptDataViewAdapter.OnItemClickListener {

    private FragmentReceiptOcrBinding binding;

    private Stack<ReceiptData> receiptDataStack = new ArrayStack<>();
    private ReceiptDataViewAdapter receiptViewAdapter;
    private List<Pair<String, String>> productPricePairs = new ArrayList<>();
    private List<String> autoCompleteShopReceiptItems = new ArrayList<>();

    public ReceiptItemsFragment() {
        // Required empty public constructor
    }

    public static ReceiptItemsFragment newInstance(OcrReceiptResponse response) {
        ReceiptItemsFragment fragment = new ReceiptItemsFragment();
        Bundle args = new Bundle();
        args.putSerializable(AddReceiptActivity.ARG_OCR_RESPONSE, response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        final long city = Settings.getCityId();
        final long shop = Preference.getShopId();
        final ReceiptItemsInShopRequest request = new ReceiptItemsInShopRequest(city, shop);

        ApiHelper.makeApiRequest(request, ApiHelper::getReceiptItemsInShop,
                throwable -> {
                },
                this::onLoadShopReceiptItems, null);
    }

    private void onLoadShopReceiptItems(List<String> dbReceipts) {
        autoCompleteShopReceiptItems = new ArrayList<>(dbReceipts);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_receipt_item_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                cancel();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancel() {
        if (receiptDataStack.size() > 1) {
            receiptDataStack.pop();
            updateProductsView();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_ocr, container, false);

        OcrReceiptResponse response = (OcrReceiptResponse) getArguments().getSerializable(AddReceiptActivity.ARG_OCR_RESPONSE);
        if (response != null) {
            receiptDataStack.push(new ReceiptData(response));
            updateProductsView();
        }

        return binding.getRoot();
    }

    public void onClickNext() {
        if (receiptDataIsNotCompleted()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Предупреждение")
                    .setMessage("Незаполненые полностью строки не будут сохранены.\n" +
                            "Продолжить?")
                    .setNegativeButton("Нет", (dialog, which) -> {
                    })
                    .setPositiveButton("Да",
                            (dialog, which) ->
                                    ((AddReceiptActivity) getActivity())
                                            .onReceiptDataSaved(receiptDataStack.peek().getCompletedList()))
                    .show();
        } else {
            ((AddReceiptActivity) getActivity()).onReceiptDataSaved(receiptDataStack.peek());
        }
    }

    private boolean receiptDataIsNotCompleted() {
        final int lastIndex = receiptDataStack.peek().size() - 1;
        final ReceiptItemPriceViewItem item = receiptDataStack.peek().getReceiptItemPriceViewItem(lastIndex);
        return item.isPartEmpty();
    }

    private void updateProductsView() {
        productPricePairs.clear();
        productPricePairs.addAll(receiptDataStack.peek().getProductsPricesPairs());
        if (receiptViewAdapter == null) {
            receiptViewAdapter = new ReceiptDataViewAdapter(getActivity(), productPricePairs, this);
            binding.listReceiptData.setAdapter(receiptViewAdapter);
        } else {
            receiptViewAdapter.notifyDataSetChanged();
        }
        receiptViewAdapter.setProductSize(receiptDataStack.size());
        receiptViewAdapter.setPriceSize(receiptDataStack.size());
    }

    @Override
    public void onClickReceiptItemRemove(int pos) {
        final ReceiptData copyReceiptData = new ReceiptData(receiptDataStack.peek());
        copyReceiptData.removeReceiptItem(pos);
        receiptDataStack.push(copyReceiptData);
        updateProductsView();
    }

    @Override
    public void onClickPriceRemove(int pos) {
        final ReceiptData copyReceiptData = new ReceiptData(receiptDataStack.peek());
        copyReceiptData.removePrice(pos);
        receiptDataStack.push(copyReceiptData);
        updateProductsView();
    }

    @Override
    public void onClickItemEdit(int pos) {
        final ReceiptData copyReceiptData = new ReceiptData(receiptDataStack.peek());
        final ReceiptItemPriceViewItem item = copyReceiptData.getReceiptItemPriceViewItem(pos);
        showEditReceiptItemDialog(item,
                (receiptItem, price) -> {
                    item.setReceiptItem(receiptItem);
                    item.setPrice(price);
                    receiptDataStack.push(copyReceiptData);
                    updateProductsView();
                });
    }

    public void showEditReceiptItemDialog(ReceiptItemPriceViewItem item, OnEditTextListener callback) {

        final ReceiptItemEditDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.receipt_item_edit_dialog, null, false);

        binding.receiptItemEditText.setText(item.getReceiptItem());
        binding.priceEditText.setText(item.getPrice());

        final List<String> matches = new ArrayList<>(item.getMatches());
        matches.add(item.getSource());

        binding.receiptItemMatches.setAdapter(new ArrayAdapter<>(
                getContext(), R.layout.receipt_matches_item, matches
        ));

        binding.receiptItemMatches.setOnItemClickListener(
                (parent, view, position, id) -> {
                    final String newText = matches.get(position);
                    binding.receiptItemEditText.setText(newText);
                });

        final List<String> autoCompleteItems = new ArrayList<>(autoCompleteShopReceiptItems);
        autoCompleteItems.addAll(receiptDataStack.peek().getReceiptItems());
        binding.receiptItemEditText.setAdapter(new ArrayAdapter<>(
                getContext(), android.R.layout.simple_list_item_1, autoCompleteItems
        ));

        new AlertDialog.Builder(getContext())
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
