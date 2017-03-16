package ru.myocr.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.myocr.R;
import ru.myocr.databinding.FragmentDetailStatsBinding;

public class DetailStatsFragment extends Fragment {


    private FragmentDetailStatsBinding binding;

    public DetailStatsFragment() {
    }

    public static DetailStatsFragment newInstance() {
        DetailStatsFragment fragment = new DetailStatsFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_stats, container, false);
        binding.floatingMenu.hide(false);
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
}
