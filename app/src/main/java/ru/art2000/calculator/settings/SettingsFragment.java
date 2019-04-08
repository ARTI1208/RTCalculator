package ru.art2000.calculator.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Objects;

import ru.art2000.calculator.BuildConfig;
import ru.art2000.calculator.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private int dev = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        mContext = getActivity();

        ListPreference TabList = (ListPreference) findPreference("tab_default");
        SwitchPreferenceCompat ZeroDiv =
                (SwitchPreferenceCompat) findPreference("zero_div");
        SwitchPreferenceCompat saveConversion =
                (SwitchPreferenceCompat) findPreference("save_currency_value");
        ListPreference UnitView = (ListPreference) findPreference("unit_view");
        ListPreference AppTheme = (ListPreference) findPreference("app_theme");
        Preference AppVersion = findPreference("app_version");
        AppVersion.setSummary(BuildConfig.VERSION_NAME);
        AppVersion.setOnPreferenceClickListener(preference -> {
            dev++;
            if (dev == 1) {
                dev = 0;
                startActivity(new Intent(mContext, InfoActivity.class));
            }
            return false;
        });
        TabList.setSummary(TabList.getEntry());
        AppTheme.setSummary(AppTheme.getEntry());
        UnitView.setSummary(UnitView.getEntry());
        saveConversion.setOnPreferenceChangeListener((preference, newValue) -> {
            PrefsHelper.setShouldSaveCurrencyConversion((Boolean) newValue);
            return true;
        });
        ZeroDiv.setOnPreferenceChangeListener((preference, newValue) -> {
            PrefsHelper.setZeroDivResult((Boolean) newValue);
            return true;
        });
        TabList.setOnPreferenceChangeListener((preference, newValue) -> {
            TabList.setValue(newValue.toString());
            TabList.setSummary(TabList.getEntry());
            PrefsHelper.setDefaultTab(newValue.toString());
            return true;
        });
        UnitView.setOnPreferenceChangeListener((preference, newValue) -> {
            UnitView.setValue(newValue.toString());
            UnitView.setSummary(UnitView.getEntry());
            PrefsHelper.setUnitViewType(newValue.toString());
            PrefsHelper.setUnitViewChanged();
            return true;
        });
        AppTheme.setOnPreferenceChangeListener((preference, newValue) -> {
            AppTheme.setValue(newValue.toString());
            AppTheme.setSummary(AppTheme.getEntry());
            switch (newValue.toString()) {
                case "dark":
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme_Dark);
                    break;
                case "day_night":
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme_DayNight);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                    break;
                default:
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme);
                    break;
            }
            PrefsHelper.setAppTheme(newValue.toString());
            Intent intent = getActivity().getIntent();
            intent.setAction("ru.art2000.calculator.action.SETTINGS");
            Activity parent = getActivity();
            parent.finish();
            parent.startActivity(intent);
            getActivity().recreate();
            return true;
        });
    }
}
