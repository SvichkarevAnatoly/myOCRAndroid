package ru.myocr.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.api.ApiHelper;
import ru.myocr.databinding.FragmentSearchReceiptItemListBinding;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.model.filter.Filter;
import ru.myocr.model.filter.SearchSource;
import ru.myocr.model.filter.SearchSourceRemote;
import ru.myocr.preference.Settings;

public class SearchReceiptItemFragment extends Fragment implements SearchReceiptItemRecyclerViewAdapter.SearchReceiptItemInteractionListener {

    private Filter filter = new Filter();
    private SearchSource searchSource = new SearchSourceRemote();
    private FragmentSearchReceiptItemListBinding binding;
    private ArrayList<SearchReceiptItem> searchReceiptItems;
    private String query;

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
        setHasOptionsMenu(true);

        filter.setCity(Settings.getCity());
        ApiHelper.makeApiRequest(Settings.getCity(), ApiHelper::getShops,
                throwable -> {
                },
                this::onLoadShops, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_receipt_item_list, container, false);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        return binding.getRoot();
    }

    private void onLoadReceiptItems(List<SearchReceiptItem> searchReceiptItems) {
        this.searchReceiptItems = new ArrayList<>(searchReceiptItems);
        binding.list.setAdapter(new SearchReceiptItemRecyclerViewAdapter(searchReceiptItems, this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_fragment, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateQuery(query);
                searchView.clearFocus();
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateQuery(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_diagram) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, DetailStatsFragment.newInstance(searchReceiptItems, query), null)
                    .commit();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateQuery(String query) {
        this.query = query;
        filter.setQuery(this.query);
        query();
    }

    private void query() {
        searchSource.search(filter, new SearchSource.SearchResultCallback() {
            @Override
            public void onFailed() {
                Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<SearchReceiptItem> result) {
                onLoadReceiptItems(result);
            }
        });
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

    private void onLoadShops(List<String> shops) {
        binding.spinnerShop.setAdapter(
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, shops));
        binding.spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String shop = shops.get(position);
                filter.setShop(shop);
                query();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filter.setShop(null);
                query();
            }
        });
    }

}

