package ru.myocr.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import ru.myocr.R;
import ru.myocr.api.ApiHelper;
import ru.myocr.model.City;
import ru.myocr.model.DbModel;
import ru.myocr.model.Shop;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            ListPreference cityPreference = (ListPreference) getPreferenceScreen()
                    .findPreference(getActivity().getString(R.string.pref_key_city));
            loadCities(cityPreference);

            cityPreference.setOnPreferenceChangeListener(this::onChangedCity);
        }

        private boolean onChangedCity(Preference preference, Object newValue) {
            Shop.deleteAll(Shop.class);
            long cityId = Long.valueOf((String) newValue);

            ApiHelper.makeApiRequest(cityId, ApiHelper::getShops,
                    throwable -> {
                    },
                    Shop::putIfNotExist, null);

            return true;
        }

        private void loadCities(ListPreference cityPreference) {
            List<City> cities = DbModel.getAll(City.class);
            String[] citiesNames = new String[cities.size()];
            String[] citiesIds = new String[cities.size()];
            for (int i = 0; i < cities.size(); i++) {
                final City city = cities.get(i);
                citiesNames[i] = city.getName();
                citiesIds[i] = String.valueOf(city.getId());
            }

            cityPreference.setEntries(citiesNames);
            cityPreference.setEntryValues(citiesIds);
        }
    }
}
