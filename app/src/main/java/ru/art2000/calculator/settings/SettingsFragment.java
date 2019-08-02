package ru.art2000.calculator.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
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

        /* tabList preference */
        ListPreference tabList = findPreference("tab_default");
        if (!getResources().getBoolean(R.bool.show_settings_as_default_tab)) {
            CharSequence[] tabEntries = tabList.getEntries();
            CharSequence[] tabValues = tabList.getEntryValues();
            CharSequence[] newTabEntries = new CharSequence[tabEntries.length - 1];
            CharSequence[] newTabValues = new CharSequence[tabValues.length - 1];
            short found = 0;
            for (int i = 0; i < tabValues.length; ++i) {
                CharSequence sequence = tabValues[i];
                if (sequence.equals("settings_tab")) {
                    found = 1;
                } else {
                    newTabEntries[i - found] = tabEntries[i];
                    newTabValues[i - found] = tabValues[i];
                }
            }
            tabList.setEntries(newTabEntries);
            tabList.setEntryValues(newTabValues);
        }
        tabList.setSummary(tabList.getEntry());
        tabList.setOnPreferenceChangeListener((preference, newValue) -> {
            tabList.setValue(newValue.toString());
            tabList.setSummary(tabList.getEntry());
            String stringValue = newValue.toString();
            PrefsHelper.setDefaultTab(stringValue);
            return true;
        });

        /* appTheme preference */
        ListPreference appTheme = findPreference("app_theme");
        appTheme.setSummary(appTheme.getEntry());
        appTheme.setOnPreferenceChangeListener((preference, newValue) -> {
            appTheme.setValue(newValue.toString());
            appTheme.setSummary(appTheme.getEntry());
            switch (newValue.toString()) {
                case "dark":
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme_Dark);
                    break;
                case "day_night":
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme_DayNight);
                    break;
                case "system":
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme_System);
                    Toast.makeText(mContext, R.string.daynight_support_message, Toast.LENGTH_LONG).show();
                    break;
                case "battery":
                    Objects.requireNonNull(getActivity()).getApplication().setTheme(R.style.AppTheme_Battery);
                    Toast.makeText(mContext, R.string.daynight_support_message, Toast.LENGTH_LONG).show();
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

        /* saveCurrencyConversion preference */
        SwitchPreferenceCompat saveCurrencyConversion = findPreference("save_currency_value");
        saveCurrencyConversion.setOnPreferenceChangeListener((preference, newValue) -> {
            PrefsHelper.setShouldSaveCurrencyConversion((Boolean) newValue);
            return true;
        });

        /* zeroDiv preference */
        SwitchPreferenceCompat zeroDiv = findPreference("zero_div");
        zeroDiv.setOnPreferenceChangeListener((preference, newValue) -> {
            PrefsHelper.setZeroDivResult((Boolean) newValue);
            return true;
        });

        /* unitView preference */
        ListPreference unitView = findPreference("unit_view");
        unitView.setSummary(unitView.getEntry());
        unitView.setOnPreferenceChangeListener((preference, newValue) -> {
            unitView.setValue(newValue.toString());
            unitView.setSummary(unitView.getEntry());
            PrefsHelper.setUnitViewType(newValue.toString());
            PrefsHelper.setUnitViewChanged();
            return true;
        });

        /* appVersion preference */
        Preference appVersion = findPreference("app_version");
        appVersion.setSummary(BuildConfig.VERSION_NAME);
        appVersion.setOnPreferenceClickListener(preference -> {
            dev++;
            if (dev == 1) {
                dev = 0;
                startActivity(new Intent(mContext, InfoActivity.class));
            }
            return false;
        });
    }
}
