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
        binding.textProduct.setOnClickListener(v -> listener.onClickProduct(position));
        binding.textPrice.setOnClickListener(v -> listener.onClickPrice(position));
        return binding.getRoot();
    }

    public interface OnItemClickListener {

        void onClickProduct(int pos);

        void onClickPrice(int pos);
    }
}
