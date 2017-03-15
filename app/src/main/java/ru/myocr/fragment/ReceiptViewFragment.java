package ru.myocr.fragment;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;
import ru.myocr.R;
import ru.myocr.databinding.FragmentTicketBinding;
import ru.myocr.model.DbModel;
import ru.myocr.model.Receipt;
import ru.myocr.model.ReceiptTag;
import ru.myocr.model.Tag;

import static ru.myocr.db.ReceiptContentProvider.URI_DELETE_TAG;

public class ReceiptViewFragment extends Fragment {

    public static final String ARG_RECEIPT = "ARG_RECEIPT";

    private Receipt receipt;

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
        receipt.loadTags(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentTicketBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ticket,
                container, false);
        binding.receiptView.setReceipt(receipt);

        List<String> tagStr = new ArrayList<>();
        for (Tag tag : receipt.tags) {
            tagStr.add(tag.tag);
        }
        binding.tagGroup.setTags(tagStr);
        binding.tagGroup.setOnTagChangeListener(new TagGroup.OnTagChangeListener() {
            @Override
            public void onAppend(TagGroup tagGroup, String tag) {
                Tag newTag = new Tag(tag);
                Uri uri = DbModel.getProviderCompartment()
                        .put(DbModel.getUriHelper().getUri(Tag.class), newTag);
                ReceiptTag receiptTag = new ReceiptTag(receipt._id, Long.valueOf(uri.getLastPathSegment()));
                receiptTag.updateDb();
            }

            @Override
            public void onDelete(TagGroup tagGroup, String tag) {
                DbModel.getProviderCompartment()
                        .delete(URI_DELETE_TAG, "", receipt._id.toString(), tag);
            }
        });

        return binding.getRoot();
    }
}