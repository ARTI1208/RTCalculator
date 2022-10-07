package ru.art2000.extensions.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;

import ru.art2000.calculator.CalculatorApplication;
import ru.art2000.calculator.R;
import ru.art2000.calculator.view.settings.PreferenceKeys;
import ru.art2000.helpers.PrefsHelper;

public class AutoThemeActivity extends DayNightActivity {

    private final SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        switch (key) {
            case PreferenceKeys.KEY_DYNAMIC_COLORS:
            case PreferenceKeys.KEY_APP_THEME:
                recreate();
                break;
            case PreferenceKeys.KEY_AUTO_DARK_THEME:
                if (isDarkThemeApplied()) {
                    recreate();
                }
                break;
            case PreferenceKeys.KEY_DARK_THEME_ACTIVATION:
            case PreferenceKeys.KEY_DARK_THEME_DEACTIVATION:
                requestThemeCheck();
                break;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyTheming();
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void applyTheming() {
        if (CalculatorApplication.DYNAMIC_COLORS_AVAILABLE && PrefsHelper.areDynamicColorsEnabled()) {
            setTheme(R.style.RT_AppTheme_System);
            DynamicColors.applyToActivityIfAvailable(this);
        } else {
            setTheme(PrefsHelper.getAppTheme());
        }
    }
}
