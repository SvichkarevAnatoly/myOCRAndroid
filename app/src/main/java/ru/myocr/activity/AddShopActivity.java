package ru.myocr.activity;

import android.app.ProgressDialog;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ru.myocr.R;
import ru.myocr.databinding.ActivityAddShopBinding;
import ru.myocr.model.City;
import ru.myocr.viewmodel.AddShopViewModel;

public class AddShopActivity extends LifecycleActivity {

    private AddShopViewModel viewModel;
    private ActivityAddShopBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);*/

        viewModel = ViewModelProviders.of(this).get(AddShopViewModel.class);
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
}
