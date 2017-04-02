package ru.myocr.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ru.myocr.R;
import ru.myocr.model.City;

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
        }

        private void loadCities(ListPreference cityPreference) {
            List<City> cities = City.getCities();
            List<String> citiesIds = new ArrayList<>();
            for (City city : cities) {
                citiesIds.add(city.id);
            }

            String[] ids = new String[citiesIds.size()];
            cityPreference.setEntries(citiesIds.toArray(ids));
            cityPreference.setEntryValues(citiesIds.toArray(ids));
        }
    }
}
