package ru.myocr.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.myocr.R;
import ru.myocr.databinding.FragmentDetailStatsBinding;
import ru.myocr.model.DbModel;
import ru.myocr.model.DummyReceipt;
import ru.myocr.model.ReceiptItem;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.util.ColorUtil;
import ru.myocr.util.RxUtil;

public class DetailStatsFragment extends Fragment {

    public static final String KEY_ITEMS = "KEY_ITEMS";
    public static final String KEY_LABEL = "KEY_LABEL";

    private FragmentDetailStatsBinding binding;
    private ArrayList<SearchReceiptItem> items;
    private String label;

    public DetailStatsFragment() {
    }

    public static DetailStatsFragment newInstance(ArrayList<SearchReceiptItem> items, String label) {
        DetailStatsFragment fragment = new DetailStatsFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_ITEMS, items);
        args.putString(KEY_LABEL, label);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        items = (ArrayList<SearchReceiptItem>) getArguments().getSerializable(KEY_ITEMS);
        label = getArguments().getString(KEY_LABEL);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_stats, container, false);
        binding.floatingMenu.hide(false);

        initLineChart();
        return binding.getRoot();
    }

    public void showFab() {
        if (binding != null) {
            binding.floatingMenu.postDelayed(() -> binding.floatingMenu.show(true), 200);
        }
    }

    public void hideFab() {
        if (binding != null) {
            binding.floatingMenu.hide(true);
        }
    }

    private void initLineChart() {
        LineChart lineChart = binding.lineChart;

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setLabelRotationAngle(-45);
        lineChart.getXAxis().setValueFormatter((value, axis) -> new SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(new Date((long) value)));
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);

        lineChart.getDescription().setEnabled(false);


        if (null == items) {
            for (int i = 0; i < 10; i++) {
                try {
                    addDataSet(DummyReceipt.DUMMY_PRODUCTS.get(i));
                } catch (Exception e) {
                }
            }
        } else {
            List<Entry> entries = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Collections.sort(items, (o1, o2) -> {
                o1.getDate();
                try {
                    return format.parse(o1.getDate()).after(format.parse(o2.getDate())) ? 1 : -1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 1;
            });
            for (SearchReceiptItem item : items) {
                try {
                    entries.add(new Entry(format.parse(item.getDate()).getTime(), item.getPrice() / 100f));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            addDataSet(entries, label);
        }
    }

    private void addDataSet(String receiptName) {
        List<Entry> entries = new ArrayList<>();

        RxUtil.work(() -> {
            List<ReceiptItem> receiptItems = DbModel.getProviderCompartment().query(ReceiptItem.URI, ReceiptItem.class)
                    .withSelection("title = ?", receiptName).list();
            Collections.sort(receiptItems, (o1, o2) -> o1.date.after(o2.date) ? 1 : -1);
            for (ReceiptItem receiptItem : receiptItems) {
                entries.add(new Entry(receiptItem.date.getTime(), receiptItem.price / 100));
            }
            return receiptItems;
        }, Throwable::printStackTrace, receiptItems -> {
            addDataSet(entries, receiptName);
        });
    }

    private void addDataSet(List<Entry> entries, String name) {
        if (entries.size() == 0) {
            return;
        }
        LineDataSet dataSet = new LineDataSet(entries, name);
        dataSet.setLineWidth(2f);

        int num = 0;
        dataSet.setDrawCircleHole(false);

        LineData lineData = binding.lineChart.getLineData();
        if (lineData == null) {
            lineData = new LineData(dataSet);
        } else {
            num = lineData.getDataSetCount();
            lineData.addDataSet(dataSet);
        }

        int idx = num % ColorUtil.CHART_COLOR.length;
        int color = ColorUtil.CHART_COLOR[idx];
        dataSet.setColor(color);

        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setFillColor(color);
        dataSet.setDrawCircleHole(false);
        dataSet.setFillAlpha(30);
        dataSet.setDrawFilled(true);

        binding.lineChart.setData(lineData);
        binding.lineChart.invalidate();
    }
}
