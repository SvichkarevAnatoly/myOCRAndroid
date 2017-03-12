package ru.myocr.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import ru.myocr.R;
import ru.myocr.databinding.ReceiptViewItemBinding;
import ru.myocr.databinding.ReceiptViewLayoutBinding;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;

/**
 * Created by bs_oydopova on 12.03.2017.
 */

public class ReceiptView extends LinearLayout {

    private ReceiptViewLayoutBinding binding;
    private Receipt receipt;

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
        binding.marketName.setText(receipt.market.title);
        binding.marketAddress.setText(receipt.market.address);
        binding.inn.setText("ИНН" + receipt.market.inn);

        for (int i = 0; i < receipt.items.size(); i++) {
            ReceiptViewItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                    R.layout.receipt_view_item, binding.items, false);
            itemBinding.num.setText(String.format("%d", i));
            ReceiptItem item = receipt.items.get(i);
            itemBinding.amount.setText(String.format("%.3f", item.amount));
            itemBinding.name.setText(item.title);
            itemBinding.price.setText(String.format("%.2f", item.price / 100.));
            binding.items.addView(itemBinding.getRoot());
        }
    }

    public ReceiptView(Context context) {
        super(context);
        init();
    }

    public ReceiptView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReceiptView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ReceiptView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.receipt_view_layout, this, true);
    }

}
