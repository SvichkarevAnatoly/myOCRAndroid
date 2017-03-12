package ru.myocr.fragment;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.myocr.App;
import ru.myocr.R;
import ru.myocr.databinding.ReceiptListItemBinding;
import ru.myocr.model.Receipt;

public class TicketRecyclerViewAdapter extends RecyclerView.Adapter<TicketRecyclerViewAdapter.ViewHolder> {

    private final List<Receipt> mValues;
    private final TicketFragment.TicketFragmentInteractionListener mListener;

    public TicketRecyclerViewAdapter(List<Receipt> items,
                                     TicketFragment.TicketFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReceiptListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.receipt_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.binding.market.setText(mValues.get(position).market.title);
        holder.binding.sum.setText(String.format("%.2f руб.", mValues.get(position).total_cost_sum / 100.));

        Date date = mValues.get(position).date;
        holder.binding.date.setText(new SimpleDateFormat("EEE, MMM d, yy", Locale.getDefault()).format(date));

        holder.binding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClickTicketItem(holder.mItem);
            }
        });

        holder.binding.getRoot().setBackgroundColor(App.getContext().getResources()
                .getColor(position % 2 == 0
                ? R.color.receipt_item_bg_light
                : R.color.receipt_item_bg_dark));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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
