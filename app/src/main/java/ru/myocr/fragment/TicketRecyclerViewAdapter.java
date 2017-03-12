package ru.myocr.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.myocr.model.Receipt;
import ru.myocr.test.R;

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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.format("%d", mValues.get(position).id));
        holder.mContentView.setText(mValues.get(position).toString());

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onClickTicketItem(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Receipt mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
