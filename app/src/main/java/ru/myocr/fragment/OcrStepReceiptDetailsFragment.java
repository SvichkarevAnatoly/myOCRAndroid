package ru.myocr.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;

import static ru.myocr.activity.ReceiptOcrActivity.ARG_OCR_RESPONSE;


public class OcrStepReceiptDetailsFragment extends Fragment {

    private OcrReceiptResponse response;

    public OcrStepReceiptDetailsFragment() {
        // Required empty public constructor
    }

    public static OcrStepReceiptDetailsFragment newInstance(OcrReceiptResponse response) {
        OcrStepReceiptDetailsFragment fragment = new OcrStepReceiptDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OCR_RESPONSE, response);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        response = (OcrReceiptResponse) getArguments().getSerializable(ARG_OCR_RESPONSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ocr_step_receipt_details, container, false);
    }

}
