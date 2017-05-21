package ru.myocr.fragment;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.myocr.App;
import ru.myocr.R;
import ru.myocr.databinding.ReceiptListItemBinding;
import ru.myocr.model.Receipt;
import ru.myocr.util.MockCursorRecyclerViewAdapter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class TicketRecyclerViewAdapter extends MockCursorRecyclerViewAdapter<TicketRecyclerViewAdapter.ViewHolder> {

    private final TicketFragment.TicketFragmentInteractionListener mListener;
    private int lastPosition = -1;

    public TicketRecyclerViewAdapter(Context context, Cursor cursor,
                                     TicketFragment.TicketFragmentInteractionListener mListener) {
        super(context, cursor);
        this.mListener = mListener;
        setHasStableIds(true);
    }


    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex("_id"));
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(TicketRecyclerViewAdapter.ViewHolder holder, Cursor cursor) {
        if (cursor == null) {
            holder.binding.shop.setText("                         ");
            holder.binding.date.setText("        ");
            holder.binding.sum.setText("            ");
            int color = Color.parseColor("#dddddd");
            holder.binding.shop.setBackgroundColor(color);
            holder.binding.date.setBackgroundColor(color);
            holder.binding.sum.setBackgroundColor(color);

            holder.binding.getRoot().setBackgroundColor(App.getContext().getResources()
                    .getColor(holder.getLayoutPosition() % 2 == 0
                            ? R.color.receipt_item_bg_light
                            : R.color.receipt_item_bg_dark));
            holder.isMock = true;
            return;
        } else {
            holder.binding.shop.setBackgroundColor(Color.parseColor("#00000000"));
            holder.binding.date.setBackgroundColor(Color.parseColor("#00000000"));
            holder.binding.sum.setBackgroundColor(Color.parseColor("#00000000"));
        }

        Receipt receipt = cupboard().withCursor(cursor).get(Receipt.class);
        receipt.loadReceiptItems(App.getContext());

        holder.mItem = receipt;
        holder.binding.shop.setText(receipt.market.title);

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

        if (!holder.isAnimated) {
            if (holder.isMock) {
                setAnimation(holder.binding.layout, cursor.getPosition());
                holder.isMock = false;
            } else {
                setAnimation(holder.binding.getRoot(), cursor.getPosition());
            }
        }
    }

    @Override
    public TicketRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ReceiptListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.receipt_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.fade_in);
        viewToAnimate.startAnimation(animation);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Receipt mItem;
        private boolean isMock = false;
        private boolean isAnimated = false;
        private ReceiptListItemBinding binding;


        public ViewHolder(ReceiptListItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void clearAnimation() {
            binding.getRoot().clearAnimation();
        }

    }
}
