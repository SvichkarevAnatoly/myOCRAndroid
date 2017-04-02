package ru.myocr.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.myocr.R;
import ru.myocr.model.SearchReceiptItem;

public class SearchReceiptItemFragment extends Fragment implements SearchReceiptItemRecyclerViewAdapter.SearchReceiptItemInteractionListener {

    public SearchReceiptItemFragment() {
    }

    @SuppressWarnings("unused")
    public static SearchReceiptItemFragment newInstance() {
        SearchReceiptItemFragment fragment = new SearchReceiptItemFragment();
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
        View view = inflater.inflate(R.layout.fragment_searchreceiptitem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final ArrayList<SearchReceiptItem> items = new ArrayList<>();
            items.add(new SearchReceiptItem("item1", 1, "2016"));
            items.add(new SearchReceiptItem("item2", 1, "2016"));
            recyclerView.setAdapter(new SearchReceiptItemRecyclerViewAdapter(items, this));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onListFragmentInteraction(SearchReceiptItem item) {

    }

}
