package ru.art2000.extensions.activities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

import ru.art2000.calculator.R;
import ru.art2000.helpers.PrefsHelper;

@SuppressWarnings("RedundantSuppression")
@SuppressLint("Registered")
public class DayNightActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        int theme = PrefsHelper.getAppTheme();
        if (theme == R.style.RT_AppTheme_DayNight && isResumeNightModeChangeEnabled()) {
            int newMode;

            if (nightModeCondition()) {
                newMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                newMode = AppCompatDelegate.MODE_NIGHT_NO;
            }

            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onResumeNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        } else if (theme == R.style.RT_AppTheme_System) {
            int newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onResumeNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        } else if (theme == R.style.RT_AppTheme_Battery) {
            int newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onResumeNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        }
    }

    @Override
    public void setTheme(int resId) {
        int newMode;
        if (resId == R.style.RT_AppTheme_DayNight && isSetThemeNightModeChangeEnabled()) {
            if (nightModeCondition()) {
                newMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                newMode = AppCompatDelegate.MODE_NIGHT_NO;
            }
        } else if (resId == R.style.RT_AppTheme_System) {
            newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else if (resId == R.style.RT_AppTheme_Battery) {
            newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
        } else if (resId == R.style.RT_AppTheme_Dark) {
            newMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            newMode = AppCompatDelegate.MODE_NIGHT_NO;
        }

        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            onSetThemeNightModeChanged(newMode);
            AppCompatDelegate.setDefaultNightMode(newMode);
        }

        super.setTheme(resId);
    }

    @SuppressWarnings("unused")
    protected void onResumeNightModeChanged(int mode) {

    }

    protected boolean isResumeNightModeChangeEnabled() {
        return true;
    }

    @SuppressWarnings("unused")
    protected void onSetThemeNightModeChanged(int mode) {

    }

    protected boolean isSetThemeNightModeChangeEnabled() {
        return true;
    }

    protected boolean nightModeCondition() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour <= 7 || hour >= 22;
    }

    public boolean isDarkThemeApplied() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        Log.e("nightMode", String.valueOf(nightModeFlags));

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
