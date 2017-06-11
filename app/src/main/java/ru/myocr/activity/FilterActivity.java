package ru.myocr.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.myocr.R;
import ru.myocr.api.ApiHelper;
import ru.myocr.databinding.ActivityFilterBinding;
import ru.myocr.databinding.FilterDialogLayoutBinding;
import ru.myocr.fragment.SearchReceiptItemRecyclerViewAdapter;
import ru.myocr.model.SearchReceiptItem;
import ru.myocr.model.Shop;
import ru.myocr.model.filter.Filter;
import ru.myocr.model.filter.SearchSource;
import ru.myocr.model.filter.SearchSourceRemote;
import ru.myocr.preference.Settings;

public class FilterActivity extends AppCompatActivity implements SearchReceiptItemRecyclerViewAdapter.SearchReceiptItemInteractionListener {

    public static final String KEY_RESULT_ITEMS = "KEY_RESULT_ITEMS";
    public static final String KEY_RESULT_FILTER = "KEY_RESULT_FILTER";

    private ActivityFilterBinding binding;

    private SearchSource searchSource = new SearchSourceRemote();
    private Filter filter = new Filter();
    private ArrayList<SearchReceiptItem> searchReceiptItems;
    private List<Shop> shops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initSearchView();

        filter.setCityId(Settings.getCityId());
        ApiHelper.makeApiRequest(Settings.getCityId(), ApiHelper::getShops,
                throwable -> {
                },
                this::onLoadShops, null);
        onLoadShops(new ArrayList<>());
        query();

        setResult(RESULT_CANCELED);
    }

    private void initSearchView() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter.setQuery(s.toString());
                query();
            }
        });
        binding.editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.editTextSearch.clearFocus();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
            return true;
        });
    }

    private void query() {
        binding.progress.setVisibility(View.VISIBLE);
        searchSource.search(filter, new SearchSource.SearchResultCallback() {
            @Override
            public void onFailed() {
                Toast.makeText(FilterActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                binding.progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(List<SearchReceiptItem> result) {
                onLoadReceiptItems(result);
                binding.progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void onLoadReceiptItems(List<SearchReceiptItem> searchReceiptItems) {
        this.searchReceiptItems = new ArrayList<>(searchReceiptItems);
        binding.list.setAdapter(new SearchReceiptItemRecyclerViewAdapter(searchReceiptItems, this));
    }

    @Override
    public void onListFragmentInteraction(SearchReceiptItem item) {
        binding.editTextSearch.setText(item.getItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_ok:
                onClickApply();
                break;
            case R.id.action_filter:
                onClickFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickApply() {
        Intent data = new Intent();
        data.putExtra(KEY_RESULT_ITEMS, new ArrayList<>(searchReceiptItems));
        data.putExtra(KEY_RESULT_FILTER, filter);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onClickFilter() {
        FilterDialogLayoutBinding binding = DataBindingUtil
                .inflate(getLayoutInflater(), R.layout.filter_dialog_layout, null, false);

        Calendar dateStart = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();

        setListenerForDateEditText(dateStart, binding.dateStart);
        setListenerForDateEditText(dateEnd, binding.dateEnd);

        List<String> names = new ArrayList<>();

        final long[] shopId = {-1};

        for (Shop shop : shops) {
            names.add(shop.getName());
        }
        binding.shop.setText(filter.getShopId() != -1 ? "" + filter.getShopId() : "");
        binding.shop.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setItems(names.toArray(new String[names.size()]), (dialog, which) -> {
                    binding.shop.setText(shops.get(which).getName());
                    shopId[0] = shops.get(which).getId();
                    dialog.dismiss();
                })
                .show());

        AppCompatDialog dialog = new AppCompatDialog(this, R.style.DialogTheme) {
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu_filter_dialog, menu);
                return true;
            }

            @Override
            public boolean onMenuItemSelected(int featureId, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_ok:
                        filter.setShopId(shopId[0]);
                        query();
                        dismiss();
                        break;
                    case android.R.id.home:
                        dismiss();
                        break;
                }
                return true;
            }

            @Override
            public boolean onPrepareOptionsMenu(Menu menu) {
                return super.onPrepareOptionsMenu(menu);
            }

        };
        dialog.setContentView(binding.getRoot());

        ActionBar actionBar = dialog.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Настройки Фильтра");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_forward_white_18dp);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        dialog.show();
    }

    private void setListenerForDateEditText(Calendar date, EditText editText) {
        DatePickerDialog.OnDateSetListener listener = (view, year, monthOfYear, dayOfMonth) -> {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "dd.MM.yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            editText.setText(sdf.format(date.getTime()));
        };

        editText.setOnClickListener(v -> {
            new DatePickerDialog(this, listener, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void onLoadShops(List<Shop> shops) {
        this.shops = shops;
    }
}

