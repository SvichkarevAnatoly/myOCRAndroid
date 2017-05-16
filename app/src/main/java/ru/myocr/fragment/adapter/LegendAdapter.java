package ru.myocr.fragment.adapter;


import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.LegendEntry;

import java.util.List;

import ru.myocr.R;
import ru.myocr.databinding.LegendItemBinding;

public abstract class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.LegendViewHolder> {

    private List<LegendEntry> legends;

    public LegendAdapter(List<LegendEntry> legends) {
        this.legends = legends;
    }

    public List<LegendEntry> getLegends() {
        return legends;
    }

    public void setLegends(List<LegendEntry> legends) {
        this.legends = legends;
    }

    @Override
    public LegendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LegendItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.legend_item, parent, false);
        return new LegendViewHolder(binding, binding.getRoot());
    }

    @Override
    public void onBindViewHolder(LegendViewHolder holder, int position) {
        LegendEntry entry = legends.get(position);
        holder.binding.color.setBackgroundColor(entry.formColor);
        holder.binding.text.setText(entry.label);

        holder.binding.delete.setOnClickListener(v -> onClickDelete(entry.label));
    }

    public abstract void onClickDelete(String label);

    @Override
    public int getItemCount() {
        return legends.size();
    }

    public class LegendViewHolder extends RecyclerView.ViewHolder {

        public LegendItemBinding binding;

        public LegendViewHolder(LegendItemBinding binding, View itemView) {
            super(itemView);
            this.binding = binding;
        }
    }

}
