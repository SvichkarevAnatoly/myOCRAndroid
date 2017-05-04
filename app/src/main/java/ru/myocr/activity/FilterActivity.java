package ru.myocr.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initSearchView();

        filter.setCity(Settings.getString(Settings.CITY));
        ApiHelper.makeApiRequest(Settings.getString(Settings.CITY), ApiHelper::getShops,
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
        searchSource.search(filter, new SearchSource.SearchResultCallback() {
            @Override
            public void onFailed() {
                Toast.makeText(FilterActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<SearchReceiptItem> result) {
                onLoadReceiptItems(result);
            }
        });
    }

    private void onLoadReceiptItems(List<SearchReceiptItem> searchReceiptItems) {
        this.searchReceiptItems = new ArrayList<>(searchReceiptItems);
        binding.list.setAdapter(new SearchReceiptItemRecyclerViewAdapter(searchReceiptItems, this));
    }

    @Override
    public void onListFragmentInteraction(SearchReceiptItem item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

        new AlertDialog.Builder(this)
                .setTitle("Дополнительные настройки")
                .setPositiveButton("Применить", (dialog, which) -> {
                })
                .setNegativeButton("Отменить", null)
                .setView(binding.getRoot())
                .show();
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

    private void onLoadShops(List<String> shops) {
        /*shops.add(0, "Не выбрано");
        binding.spinnerShop.setAdapter(
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shops));
        binding.spinnerShop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String shop = 0 != position ? shops.get(position) : null;
                filter.setShop(shop);
                query();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filter.setShop(null);
                query();
            }
        });*/
    }
}

