package ru.myocr.fragment;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ru.myocr.R;
import ru.myocr.api.ocr.OcrReceiptResponse;
import ru.myocr.databinding.FragmentOcrStepReceiptDetailsBinding;

import static ru.myocr.activity.ReceiptOcrActivity.ARG_OCR_RESPONSE;


public class OcrStepReceiptDetailsFragment extends Fragment {

    private OcrReceiptResponse response;
    private FragmentOcrStepReceiptDetailsBinding binding;
    private Calendar date;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ocr_step_receipt_details, container, false);
        initUi();
        return binding.getRoot();
    }

    public void onClickSave() {

    }

    public void initUi() {
        date = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            this.date.set(Calendar.YEAR, year);
            this.date.set(Calendar.MONTH, monthOfYear);
            this.date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel();
        };

        binding.date.setOnClickListener(v -> new DatePickerDialog(getActivity(), date, this.date
                .get(Calendar.YEAR), this.date.get(Calendar.MONTH),
                this.date.get(Calendar.DAY_OF_MONTH)).show());

        updateLabel();
    }

    private void updateLabel() {

        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.date.setText(sdf.format(date.getTime()));
    }
}
