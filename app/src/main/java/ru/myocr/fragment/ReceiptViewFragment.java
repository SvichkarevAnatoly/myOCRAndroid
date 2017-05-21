package ru.myocr.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.myocr.R;
import ru.myocr.databinding.FragmentTicketBinding;
import ru.myocr.model.DbModel;
import ru.myocr.model.Receipt;

public class ReceiptViewFragment extends Fragment {

    public static final String ARG_RECEIPT = "ARG_RECEIPT";

    private Receipt receipt;
    private FragmentTicketBinding binding;

    public ReceiptViewFragment() {
    }

    public static ReceiptViewFragment newInstance(long receiptId) {
        ReceiptViewFragment fragment = new ReceiptViewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RECEIPT, receiptId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long id = getArguments().getLong(ARG_RECEIPT, -1);
        receipt = DbModel.byId(Receipt.URI, id, Receipt.class);
        receipt.loadReceiptItems(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ticket,
                container, false);
        binding.receiptView.setReceipt(receipt);

        return binding.getRoot();
    }
}