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

import ru.myocr.model.R;
import ru.myocr.model.databinding.ReceiptItemBinding;

public class ReceiptDataViewAdapter extends ArrayAdapter<Pair<String, String>> {

    private final OnItemClickListener listener;

    public ReceiptDataViewAdapter(Context context, List<Pair<String, String>> receipts,
                                  OnItemClickListener listener) {
        super(context, 0, receipts);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReceiptItemBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.receipt_item, parent, false);
        final Pair<String, String> item = getItem(position);
        binding.textProduct.setText(item.first);
        binding.textPrice.setText(item.second);

        binding.buttonProductUp.setOnClickListener(v -> listener.onClickProductUp(position));
        binding.buttonProductDown.setOnClickListener(v -> listener.onClickProductDown(position));
        binding.buttonProductRemove.setOnClickListener(v -> listener.onClickProductRemove(position));

        binding.buttonPriceDown.setOnClickListener(v -> listener.onClickPriceDown(position));
        binding.buttonPriceRemove.setOnClickListener(v -> listener.onClickPriceRemove(position));

        if (position == 0) {
            binding.buttonProductUp.setVisibility(View.INVISIBLE);
        }
        if (position == getCount() - 1) {
            binding.buttonProductDown.setVisibility(View.INVISIBLE);
            binding.buttonPriceDown.setVisibility(View.INVISIBLE);
        }
        return binding.getRoot();
    }

    public interface OnItemClickListener {

        void onClickProductRemove(int pos);

        void onClickPriceRemove(int pos);

        void onClickProductDown(int pos);

        void onClickProductUp(int pos);

        void onClickPriceDown(int pos);
    }
}
