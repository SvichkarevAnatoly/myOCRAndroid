package ru.myocr.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.databinding.ActivityAddShopBinding;
import ru.myocr.model.City;
import ru.myocr.viewmodel.DataSource;
import ru.myocr.viewmodel.ShopAddingViewModel;
import ru.myocr.viewmodel.ShopDataSourceImpl;

public class ShopAddingActivity extends LifecycleActivity {

    private ShopAddingViewModel viewModel;
    private ActivityAddShopBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);*/

        final ShopDataSourceImpl dataSource = new ShopDataSourceImpl();
        final ShopAddingViewModelFactory factory = new ShopAddingViewModelFactory(dataSource);
        viewModel = ViewModelProviders.of(this, factory).get(ShopAddingViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shop);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding city. Please wait.");
        progressDialog.setCancelable(false);

        binding.addButton.setOnClickListener(v -> viewModel.addCity("", 0));

        observeModel();
    }

    private void observeModel() {
        viewModel.getCities().observe(this, cityList -> {
            ArrayList<String> cities = new ArrayList<>();
            for (City city : cityList) {
                cities.add(city.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
            binding.citySpinner.setAdapter(adapter);
        });

        viewModel.getProgress().observe(this, progress -> {
            if (progress) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                viewModel.addCity(
                        binding.editTextShopName.getEditableText().toString(),
                        binding.citySpinner.getSelectedItemPosition());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ShopAddingViewModelFactory implements ViewModelProvider.Factory {

        private DataSource<List<City>> dataSource;

        public ShopAddingViewModelFactory(DataSource<List<City>> dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new ShopAddingViewModel(dataSource);
        }
    }
}
