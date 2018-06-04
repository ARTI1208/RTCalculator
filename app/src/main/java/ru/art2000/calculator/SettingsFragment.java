package ru.art2000.calculator;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatDelegate;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences.Editor prefsEd;
    SharedPreferences prefs;
//    ListPreference TabList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        ListPreference TabList = (ListPreference) findPreference("tab_default");
        SwitchPreference ZeroDiv = (SwitchPreference) findPreference("zero_div");
        ListPreference AppTheme = (ListPreference) findPreference("app_theme");
        if (prefs.getBoolean("is_first_run", false))
            AppTheme.setValue("light");
//              prefsEd = prefs.edit().putBoolean("zero_div", false);
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
//        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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
//                if (newValue.toString().equals("dark"))
//                    getActivity().getApplication().setTheme(R.style.AppTheme_Dark);
//                else
//                    getActivity().getApplication().setTheme(R.style.AppTheme);
//                    setTheme(R.style.AppTheme_Dark);


                prefsEd = prefs.edit().putBoolean("theme_changed", true);
                prefsEd.apply();
                getActivity().recreate();
//                BottomNavigationView navigation = getActivity().findViewById(R.id.navigation);
//                navigation.setSelectedItemId(R.id.navigation_settings);

                return true;
            }
        });
        prefsEd.apply();
    }



//    ListPreference TabList1
//    @Override
//    public boolean onPreferenceChangeListner(Preference preference, Object value) {
//        ListPreference TabList = (ListPreference) findPreference("tab_default");
//        CharSequence TabText = TabList.getEntry();
//        TabList.setSummary(TabText);
//        return true;
//    }

}
