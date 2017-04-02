package ru.myocr.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ru.myocr.R;
import ru.myocr.api.ApiHelper;
import ru.myocr.api.SearchReceiptItemsRequest;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.preference.Preference;
import ru.myocr.preference.Settings;

public class SearchReceiptItemFragment extends Fragment implements SearchReceiptItemRecyclerViewAdapter.SearchReceiptItemInteractionListener {

    private RecyclerView recyclerView;

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
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            final String city = Settings.getString(Settings.CITY);
            final String shop = Preference.getString(Preference.SHOP);
            final SearchReceiptItemsRequest request = new SearchReceiptItemsRequest(city, shop);
            ApiHelper.makeApiRequest(request, ApiHelper::getReceiptItems,
                    throwable -> Toast.makeText(getContext(), "Ошибка получения данных", Toast.LENGTH_SHORT).show(),
                    this::onLoadReceiptItems, null);
        }
        return view;
    }

    private void onLoadReceiptItems(List<SearchReceiptItem> searchReceiptItems) {
        recyclerView.setAdapter(new SearchReceiptItemRecyclerViewAdapter(searchReceiptItems, this));
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
