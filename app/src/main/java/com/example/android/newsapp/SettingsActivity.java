package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);

            Preference orderBy = findPreference(getString(R.string.settingsOrderByKey));
            setPreferenceValue(orderBy);

            Preference pageSize = findPreference(getString(R.string.settingsPageSizeKey));
            setPreferenceValue(pageSize);
        }

        private void setPreferenceValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String settingValue = sharedPrefs.getString(preference.getKey(), "");

            onPreferenceChange(preference, settingValue);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String settingValue = o.toString();

            String key = preference.getKey();

            if(key.equals(getString(R.string.settingsPageSizeKey)) &&
                    TextUtils.isDigitsOnly(settingValue)) {
                int pageSize = Integer.parseInt(settingValue);

                if(pageSize < 1 || pageSize > 50) {
                    return false;
                }
            }

            if(!(preference instanceof ListPreference)) {
                preference.setSummary(settingValue);
                return true;
            }

            ListPreference listPreference = (ListPreference)preference;

            int prefIndex = listPreference.findIndexOfValue(settingValue);

            if(prefIndex >= 0) {
                CharSequence[] entries = listPreference.getEntries();
                preference.setSummary(entries[prefIndex]);
            }

            return true;
        }
    }
}