package ru.myocr.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            loadCities();
        }

        private void loadCities() {
            ListPreference preference = (ListPreference) getPreferenceScreen()
                    .findPreference(getActivity().getString(R.string.pref_key_city));

            List<City> cities = City.getCities();
            List<String> citiesIds = new ArrayList<>();
            for (City city : cities) {
                citiesIds.add(city.id);
            }

            String[] ids = new String[citiesIds.size()];
            preference.setEntries(citiesIds.toArray(ids));
            preference.setEntryValues(citiesIds.toArray(ids));
        }

    }
}
