package com.node22.breadcrumbs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference("publish_location"));
        bindPreferenceSummaryToValue(findPreference("publish_url"));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        Object value = null;

        SharedPreferences sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(preference.getContext());

        if (preference instanceof CheckBoxPreference) {
            value = sharedPreferences.getBoolean(preference.getKey(), false);
        } else if (preference instanceof EditTextPreference) {
            value = sharedPreferences.getString(preference.getKey(), "");
        }

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference, value);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = "Unknown";

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof CheckBoxPreference) {
            Boolean booleanValue = (Boolean)value;
            stringValue = booleanValue ? "Enabled" : "Disabled";

        } else if (preference instanceof EditTextPreference) {
            stringValue = value.toString();
        }

        Util.debug(stringValue);
        preference.setSummary(stringValue);

        return true;
    }
}
