package ru.myocr.fragment;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.myocr.App;
import ru.myocr.R;
import ru.myocr.databinding.ReceiptListItemBinding;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;
import ru.myocr.util.CursorRecyclerViewAdapter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class TicketRecyclerViewAdapter extends CursorRecyclerViewAdapter<TicketRecyclerViewAdapter.ViewHolder> {

    private final TicketFragment.TicketFragmentInteractionListener mListener;

    public TicketRecyclerViewAdapter(Context context, Cursor cursor,
                                     TicketFragment.TicketFragmentInteractionListener mListener) {
        super(context, cursor);
        this.mListener = mListener;
    }

    @Override
    public void onBindViewHolder(TicketRecyclerViewAdapter.ViewHolder holder, Cursor cursor) {
        Receipt receipt = cupboard().withCursor(cursor).get(Receipt.class);
        receipt.loadReceiptItems(App.getContext());

        holder.mItem = receipt;
        holder.binding.market.setText(receipt.market.title);

        Locale locale = new Locale("ru","RU");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        String totalCostSum = format.format(receipt.totalCostSum / 100.);

        holder.binding.sum.setText(totalCostSum);

        Date date = receipt.date;
        holder.binding.date.setText(new SimpleDateFormat("EEE, d MMM, yyyy", locale).format(date));

        holder.binding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClickTicketItem(holder.mItem);
            }
        });

        holder.binding.getRoot().setOnLongClickListener(v -> {
            if (null != mListener) {
                mListener.onLongClickTicketItem(holder.mItem);
            }
            return true;
        });

        holder.binding.getRoot().setBackgroundColor(App.getContext().getResources()
                .getColor(cursor.getPosition() % 2 == 0
                        ? R.color.receipt_item_bg_light
                        : R.color.receipt_item_bg_dark));
    }

    @Override
    public TicketRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReceiptListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.receipt_list_item, parent, false);
        return new ViewHolder(binding);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public Receipt mItem;
        private ReceiptListItemBinding binding;

        public ViewHolder(ReceiptListItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

    }
}
