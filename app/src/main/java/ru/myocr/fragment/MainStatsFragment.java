package ru.myocr.fragment;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.myocr.R;
import ru.myocr.databinding.FragmentMainStatsBinding;
import ru.myocr.model.DbModel;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptItem;
import ru.myocr.model.Tag;
import ru.myocr.util.RxUtil;

public class MainStatsFragment extends Fragment {

    private FragmentMainStatsBinding binding;

    public MainStatsFragment() {
    }

    public static MainStatsFragment newInstance() {
        MainStatsFragment fragment = new MainStatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_stats, container, false);

        List<ReceiptItem> currMonthReceiptItems = getCurrentMonthReceiptItems();
        long monthCosts = calculateCosts(currMonthReceiptItems) / 100;
        binding.valueMonthCosts.setText(String.valueOf(monthCosts) + "\u20BD");

        long averageReceiptTotal = getAverageReceiptTotal() / 100;
        binding.valueMeanReceipt.setText(String.valueOf(averageReceiptTotal) + "\u20BD");

        initPieChart();
        return binding.getRoot();
    }

    private void initPieChart() {
        PieChart pieChart = binding.pieChart;

        RxUtil.work(() -> {
            List<PieEntry> pieEntries = new ArrayList<>();
            List<Tag> allTags = Tag.getAllTags();

            for (Tag tag : allTags) {
                List<Receipt> receiptByTag = Receipt.findReceiptByTagId(tag._id);
                long sum = calculateReceiptCosts(receiptByTag) / 100;

                if (sum != 0) {
                    pieEntries.add(new PieEntry(sum, tag.tag));
                }
            }

            List<Receipt> receiptsWithoutTag = Receipt.findReceiptWithoutTag();
            if (receiptsWithoutTag.size() > 0) {
                long sum = calculateReceiptCosts(receiptsWithoutTag) / 100;
                pieEntries.add(new PieEntry(sum, "Остальное"));
            }

            return pieEntries;
        }, Throwable::printStackTrace, pieEntries -> {

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setValueLinePart1OffsetPercentage(90f);
            pieDataSet.setValueLinePart1Length(0.5f);
            pieDataSet.setValueLinePart2Length(0.3f);
            pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet.setSliceSpace(3f);

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            pieDataSet.setColors(colors);

            pieChart.setEntryLabelTextSize(12f);
            pieChart.animateXY(1000, 1000);
            pieChart.setExtraOffsets(20f, 0f, 20f, 0f);

            //pieChart.setCenterText(String.valueOf(totalSum));
            pieChart.setCenterTextSize(12);
            pieChart.setDrawCenterText(true);
            pieChart.setDrawEntryLabels(false);
            pieChart.setUsePercentValues(true);
            pieChart.setRotationEnabled(false);
            pieChart.setHoleRadius(40f);
            pieChart.setTransparentCircleRadius(40f);

            pieChart.getDescription().setEnabled(false);

            Legend l = pieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            PieData data = new PieData(pieDataSet);
            data.setValueFormatter(new PercentFormatter());
            pieChart.setData(data);
        });
    }

    private List<ReceiptItem> getCurrentMonthReceiptItems()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Uri uri = DbModel.getUriHelper().getUri(ReceiptItem.class);
        return DbModel.getProviderCompartment()
                .query(uri, ReceiptItem.class)
                .withSelection("date >= ?", String.valueOf(calendar.getTimeInMillis()))
                .list();
    }

    private long getAverageReceiptTotal()
    {
        Uri uri = DbModel.getUriHelper().getUri(Receipt.class);
        List<Receipt> list = DbModel.getProviderCompartment()
                .query(uri, Receipt.class)
                .list();

        return calculateReceiptCosts(list) / list.size();
    }

    private long calculateReceiptCosts(List<Receipt> items)
    {
        long sum = 0l;
        for (Receipt receipt : items) {
            sum += receipt.totalCostSum;
        }

        return sum;
    }

    private long calculateCosts(List<ReceiptItem> items)
    {
        long sum = 0l;
        for (ReceiptItem item : items) {
            sum += item.price;
        }

        return sum;
    }
}
