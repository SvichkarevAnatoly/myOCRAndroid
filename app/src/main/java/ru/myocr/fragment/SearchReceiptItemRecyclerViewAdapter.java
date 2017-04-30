package ru.myocr.fragment;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ru.myocr.App;
import ru.myocr.R;
import ru.myocr.databinding.FragmentSearchReceiptItemBinding;
import ru.myocr.model.SearchReceiptItem;


public class SearchReceiptItemRecyclerViewAdapter extends RecyclerView.Adapter<SearchReceiptItemRecyclerViewAdapter.ViewHolder> {

    private final List<SearchReceiptItem> mValues;
    private final SearchReceiptItemInteractionListener mListener;

    public SearchReceiptItemRecyclerViewAdapter(List<SearchReceiptItem> items, SearchReceiptItemInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final FragmentSearchReceiptItemBinding inflate =
                DataBindingUtil.inflate(LayoutInflater.from(App.getContext()),
                R.layout.fragment_search_receipt_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SearchReceiptItem item = mValues != null ? mValues.get(position) : null;
        if (null == item) {
            return;
        }
        holder.binding.receiptItem.setText(item.getItem());
        holder.binding.date.setText(item.getDate());

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String price = format.format(item.getPrice() / 100.);
        holder.binding.price.setText(price);
        holder.binding.getRoot().setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(item);
            }
        });

        holder.binding.getRoot().setBackgroundColor(App.getContext().getResources()
                .getColor(position % 2 == 0
                        ? R.color.receipt_item_bg_light
                        : R.color.receipt_item_bg_dark));
    }

    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        }
        return mValues.size();
    }

    public interface SearchReceiptItemInteractionListener {
        void onListFragmentInteraction(SearchReceiptItem item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public FragmentSearchReceiptItemBinding binding;

        public ViewHolder(FragmentSearchReceiptItemBinding inflate) {
            super(inflate.getRoot());
            this.binding = inflate;
        }
    }
}
