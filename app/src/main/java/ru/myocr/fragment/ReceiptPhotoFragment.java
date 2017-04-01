package ru.myocr.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.myocr.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptPhotoFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public ReceiptPhotoFragment() {
        // Required empty public constructor
    }


    public static ReceiptPhotoFragment newInstance(String param1) {
        ReceiptPhotoFragment fragment = new ReceiptPhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt_photo, container, false);
    }

}
