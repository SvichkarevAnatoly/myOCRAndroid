package ru.myocr.fragment;


import android.databinding.DataBindingUtil;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.databinding.FragmentMainStatsBinding;
import ru.myocr.model.Receipt;
import ru.myocr.model.Tag;

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

        initPieChart();
        return binding.getRoot();
    }

    private void initPieChart() {
        PieChart pieChart = binding.pieChart;

        List<PieEntry> pieEntries = new ArrayList<>();

        List<Tag> allTags = Tag.getAllTags();

        int totalSum = 0;

        for (Tag tag : allTags) {
            List<Receipt> receiptByTag = Receipt.findReceiptByTagId(tag._id);

            int sum = 0;
            for (Receipt receipt : receiptByTag) {
                sum += receipt.totalCostSum;
            }
            sum /= 100;
            if (0 == sum) {
                continue;
            }
            pieEntries.add(new PieEntry(sum, tag.tag));

            totalSum += sum;
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueLinePart1OffsetPercentage(80f);
        pieDataSet.setValueLinePart1Length(0.4f);
        pieDataSet.setValueLinePart2Length(0.6f);
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

        pieChart.setData(new PieData(pieDataSet));

        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateX(500);
        pieChart.setExtraOffsets(20f, 0f, 20f, 0f);

        pieChart.setCenterText(String.valueOf(totalSum));
        pieChart.setCenterTextSize(12);
        pieChart.setDrawCenterText(true);
        pieChart.setDrawEntryLabels(false);

        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);



    }

}
