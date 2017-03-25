package ru.myocr.fragment;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        receipt.loadTags(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ticket,
                container, false);
        binding.receiptView.setReceipt(receipt);

        List<String> tagStr = new ArrayList<>();
        for (Tag tag : receipt.tags) {
            tagStr.add(tag.tag);
        }

        boolean editMode = receipt.tags.size() == 0;
        updateTagView(editMode ? "" : receipt.tags.get(0).tag, editMode);

        binding.deleteTag.setOnClickListener(v -> {
            DbModel.getProviderCompartment()
                    .delete(URI_DELETE_TAG, "", receipt._id.toString(), binding.tagText.getText().toString());
            updateTagView("", true);
        });

        binding.tagText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENDCALL){
                    String tag = v.getText().toString();

                    Tag newTag = new Tag(tag);
                    Uri tagUri = DbModel.getUriHelper().getUri(Tag.class);
                    Tag existingTag = DbModel.getProviderCompartment()
                            .query(tagUri, Tag.class)
                            .withSelection("tag = ?", tag)
                            .get();
                    Long id;

                    if (existingTag == null) {
                        Uri uri = DbModel.getProviderCompartment().put(tagUri, newTag);
                        id = Long.valueOf(uri.getLastPathSegment());
                    }
                    else {
                        id = existingTag._id;
                    }

                    ReceiptTag receiptTag = new ReceiptTag(receipt._id, id);
                    receiptTag.updateDb();

                    updateTagView(tag, false);
                }
                return false;
            }
        });

        return binding.getRoot();
    }

    private void updateTagView(String text, boolean editMode) {
        binding.tagText.setText(text);

        if (editMode)
        {
            binding.deleteTag.setVisibility(View.GONE);
        }
        else{
            binding.deleteTag.setVisibility(View.VISIBLE);
        }

        binding.tagText.setInputType(editMode ? InputType.TYPE_CLASS_TEXT :InputType.TYPE_NULL);
    }
}