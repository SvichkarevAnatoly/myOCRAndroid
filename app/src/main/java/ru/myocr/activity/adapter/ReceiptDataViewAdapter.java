package ru.myocr.activity.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ru.myocr.R;
import ru.myocr.databinding.ReceiptItemBinding;

public class ReceiptDataViewAdapter extends ArrayAdapter<Pair<String, String>> {

    private final OnItemClickListener listener;
    private int productSize;
    private int priceSize;

    public ReceiptDataViewAdapter(Context context, List<Pair<String, String>> receipts,
                                  OnItemClickListener listener) {
        super(context, 0, receipts);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReceiptItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                        R.layout.receipt_item, parent, false);
        final Pair<String, String> item = getItem(position);
        binding.textProduct.setText(item.first != null ? item.first : "");
        binding.textPrice.setText(item.second != null ? item.second : "");

        /*binding.buttonProductUp.setOnClickListener(v -> listener.onClickReceiptItemUp(position));
        binding.buttonProductDown.setOnClickListener(v -> listener.onClickReceiptItemDown(position));*/
        binding.buttonProductRemove.setOnClickListener(v -> listener.onClickReceiptItemRemove(position));

        /*binding.buttonPriceDown.setOnClickListener(v -> listener.onClickPriceDown(position));*/
        binding.buttonPriceRemove.setOnClickListener(v -> listener.onClickPriceRemove(position));

        binding.textProduct.setOnClickListener(v -> listener.onClickItemEdit(position));
        binding.textPrice.setOnClickListener(v -> listener.onClickItemEdit(position));

        restrictVisibility(position, binding);
        return binding.getRoot();
    }

    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }

    public void setPriceSize(int priceSize) {
        this.priceSize = priceSize;
    }

    private void restrictVisibility(int position, ReceiptItemBinding binding) {
        final Pair<String, String> item = getItem(position);

        int productButtonsVisibility = item.first != null ? View.VISIBLE : View.INVISIBLE;
        int priceButtonsVisibility = item.second != null ? View.VISIBLE : View.INVISIBLE;

        /*binding.buttonProductUp.setVisibility(productButtonsVisibility);
        binding.buttonProductDown.setVisibility(productButtonsVisibility);*/
        binding.buttonProductRemove.setVisibility(productButtonsVisibility);
        /*binding.buttonPriceDown.setVisibility(priceButtonsVisibility);*/
        binding.buttonPriceRemove.setVisibility(priceButtonsVisibility);

        /*if (position == 0) {
            binding.buttonProductUp.setVisibility(View.INVISIBLE);
        }

        if (position == productSize - 1) {
            binding.buttonProductDown.setVisibility(View.INVISIBLE);
        }
        if (position == priceSize - 1) {
            binding.buttonPriceDown.setVisibility(View.INVISIBLE);
        }*/
    }

    public interface OnItemClickListener {

        void onClickReceiptItemRemove(int pos);

        void onClickPriceRemove(int pos);

        void onClickItemEdit(int pos);
    }
}
