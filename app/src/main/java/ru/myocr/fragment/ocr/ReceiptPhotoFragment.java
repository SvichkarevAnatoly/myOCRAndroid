package ru.myocr.fragment.ocr;


import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import ru.myocr.R;
import ru.myocr.databinding.FragmentReceiptPhotoBinding;
import ru.myocr.util.BitmapTransform;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptPhotoFragment extends Fragment {
    public static final int MAX_BMP_SIZE = 1000;
    private static final String ARG_PARAM1 = "param1";
    private Uri photoUri;

    public ReceiptPhotoFragment() {
        // Required empty public constructor
    }


    public static ReceiptPhotoFragment newInstance(Uri photoUri) {
        ReceiptPhotoFragment fragment = new ReceiptPhotoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, photoUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoUri = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final FragmentReceiptPhotoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_photo, container, false);
        Picasso.with(getActivity())
                .load(photoUri)
                .transform(new BitmapTransform(MAX_BMP_SIZE, MAX_BMP_SIZE))
                .into(binding.receiptSelectedPhoto);
        return binding.getRoot();
    }

}
