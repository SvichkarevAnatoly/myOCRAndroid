package ru.myocr.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;

import static ru.myocr.activity.ReceiptOcrActivity.ARG_OCR_RESPONSE;

/**
 * A simple {@link Fragment} subclass.
 */
public class OcrStepReceiptItemsFragment extends Fragment {


    private ReceiptItemsFragment receiptItemsFragment;
    private ViewPager viewPager;
    private OcrReceiptResponse response;

    public OcrStepReceiptItemsFragment() {
        // Required empty public constructor
    }

    public static OcrStepReceiptItemsFragment newInstance(OcrReceiptResponse response, Uri photo) {
        OcrStepReceiptItemsFragment fragment = new OcrStepReceiptItemsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OCR_RESPONSE, response);
        //args.putString(ARG_OCR_PHOTO, photo.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        response = (OcrReceiptResponse) getArguments().getSerializable(ARG_OCR_RESPONSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_ocr_step_receipt_items, container, false);
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) inflate.findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) inflate.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        return inflate;
    }

    public void onClickNext() {
        if (receiptItemsFragment != null) {
            receiptItemsFragment.onClickNext();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    receiptItemsFragment = ReceiptItemsFragment.newInstance(response);
                    return receiptItemsFragment;
                case 1:
                    return ReceiptPhotoFragment.newInstance(null);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Продукты";
                case 1:
                    return "Фото";
            }
            return null;
        }
    }
}
