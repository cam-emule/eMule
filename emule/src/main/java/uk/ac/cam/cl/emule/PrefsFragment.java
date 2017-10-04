package uk.ac.cam.cl.emule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.Map;

/**
 * Created by fergus on 09/12/2016.
 */


public class PrefsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = PrefsFragment.class.getSimpleName();

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //add xml
        addPreferencesFromResource(R.xml.preferences);
        onResume();

   }


    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        Map<String, ?> preferencesMap = sharedPreferences.getAll();
        // iterate through the preference entries and update their summary if they are an instance of EditTextPreference
        for (Map.Entry<String, ?> preferenceEntry : preferencesMap.entrySet()) {
            if (preferenceEntry instanceof ListPreference)
                 ((ListPreference) preferenceEntry).setSummary(((ListPreference) preferenceEntry).getEntry());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
                MainActivity.remoteIP = listPreference.getEntryValues()[prefIndex].toString();
                MainActivity.getInstance().startStatusUpdate();
            }
        } else {
            preference.setSummary(sharedPreferences.getString(key, ""));

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregister event bus.
        //EventBus.unregister(this);
    }


}