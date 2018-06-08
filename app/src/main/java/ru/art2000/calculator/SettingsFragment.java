package ru.art2000.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatDelegate;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences.Editor prefsEd;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        ListPreference TabList = (ListPreference) findPreference("tab_default");
        SwitchPreference ZeroDiv = (SwitchPreference) findPreference("zero_div");
        SwitchPreference UnitView = (SwitchPreference) findPreference("unit_view");
        ListPreference AppTheme = (ListPreference) findPreference("app_theme");
        if (prefs.getBoolean("is_first_run", false))
            AppTheme.setValue("light");
        String ThemeVal = AppTheme.getValue();
        String TabValue = TabList.getValue();
        CharSequence TabText = TabList.getEntry();
        TabList.setSummary(TabText);
        CharSequence STheme = AppTheme.getEntry();
        AppTheme.setSummary(STheme);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        prefsEd = prefs.edit().putString("tab_default", TabValue);
        prefsEd = prefs.edit().putString("app_theme", ThemeVal);
        prefsEd = prefs.edit().putBoolean("zero_div", ZeroDiv.isChecked());
        prefsEd = prefs.edit().putBoolean("unit_view", UnitView.isChecked());
        TabList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference TabList = (ListPreference) findPreference("tab_default");
                TabList.setValue(newValue.toString());
                CharSequence DTab = TabList.getEntry();
                TabList.setSummary(DTab);
                return true;
            }
        });
        AppTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference AppTheme = (ListPreference) findPreference("app_theme");
                AppTheme.setValue(newValue.toString());
                CharSequence STheme = AppTheme.getEntry();
                AppTheme.setSummary(STheme);
                switch (newValue.toString()){
                    case "dark":
                        getActivity().getApplication().setTheme(R.style.AppTheme_Dark);
                        break;
                    case "daynight":
                        getActivity().getApplication().setTheme(R.style.AppTheme_DayNight);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                        break;
                    default:
                        getActivity().getApplication().setTheme(R.style.AppTheme);
                        break;
                }
                prefsEd = prefs.edit().putBoolean("theme_changed", true);
                prefsEd.apply();
                getActivity().recreate();
                return true;
            }
        });
        prefsEd.apply();
    }
}
