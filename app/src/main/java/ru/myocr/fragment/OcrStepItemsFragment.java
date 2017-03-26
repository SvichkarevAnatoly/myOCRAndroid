package ru.myocr.fragment;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.activity.ReceiptOcrActivity;
import ru.myocr.activity.adapter.ReceiptDataViewAdapter;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.FragmentReceiptOcrBinding;
import ru.myocr.databinding.ReceiptItemEditDialogBinding;
import ru.myocr.model.ReceiptData;
import ru.myocr.model.ReceiptItemPriceViewItem;

import static ru.myocr.activity.ReceiptOcrActivity.ARG_OCR_RESPONSE;

public class OcrStepItemsFragment extends Fragment implements ReceiptDataViewAdapter.OnItemClickListener {

    private FragmentReceiptOcrBinding binding;

    private ReceiptData receiptData;
    private ReceiptDataViewAdapter receiptViewAdapter;
    private List<Pair<String, String>> productPricePairs = new ArrayList<>();

    public OcrStepItemsFragment() {
        // Required empty public constructor
    }

    public static OcrStepItemsFragment newInstance(OcrReceiptResponse response) {
        OcrStepItemsFragment fragment = new OcrStepItemsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OCR_RESPONSE, response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_ocr, container, false);

        OcrReceiptResponse response = (OcrReceiptResponse) getArguments().getSerializable(ARG_OCR_RESPONSE);
        if (response != null) {
            receiptData = new ReceiptData(response);
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
                                    ((ReceiptOcrActivity) getActivity())
                                            .onReceiptDataSaved(receiptData.getCompletedList()))
                    .show();
        } else {
            ((ReceiptOcrActivity) getActivity()).onReceiptDataSaved(receiptData);
        }
    }

    private boolean receiptDataIsNotCompleted() {
        final int lastIndex = receiptData.size() - 1;
        final ReceiptItemPriceViewItem item = receiptData.getReceiptItemPriceViewItem(lastIndex);
        return item.isPartEmpty();
    }

    private void updateProductsView() {
        productPricePairs.clear();
        productPricePairs.addAll(receiptData.getProductsPricesPairs());
        if (receiptViewAdapter == null) {
            receiptViewAdapter = new ReceiptDataViewAdapter(getActivity(), productPricePairs, this);
            binding.listReceiptData.setAdapter(receiptViewAdapter);
        } else {
            receiptViewAdapter.notifyDataSetChanged();
        }
        receiptViewAdapter.setProductSize(receiptData.size());
        receiptViewAdapter.setPriceSize(receiptData.size());
    }

    @Override
    public void onClickReceiptItemRemove(int pos) {
        receiptData.removeReceiptItem(pos);
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

        final ReceiptItemEditDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.receipt_item_edit_dialog, null, false);

        binding.receiptItemEditText.setText(item.getReceiptItem());
        binding.priceEditText.setText(item.getPrice());

        final List<String> matches = new ArrayList<>(item.getMatches());
        matches.add(item.getSource());

        binding.receiptItemMatches.setAdapter(new ArrayAdapter<>(
                getContext(), android.R.layout.simple_list_item_1, matches
        ));

        binding.receiptItemMatches.setOnItemClickListener(
                (parent, view, position, id) -> {
                    final String newText = matches.get(position);
                    binding.receiptItemEditText.setText(newText);
                });

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
