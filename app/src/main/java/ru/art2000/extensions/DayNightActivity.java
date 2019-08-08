package ru.art2000.extensions;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

import ru.art2000.calculator.R;
import ru.art2000.helpers.PrefsHelper;

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
        if (resId == R.style.RT_AppTheme_DayNight && isSetThemeNightModeChangeEnabled()) {
            int newMode;

            if (nightModeCondition()) {
                newMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                newMode = AppCompatDelegate.MODE_NIGHT_NO;
            }

            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onSetThemeNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        } else if (resId == R.style.RT_AppTheme_System) {
            int newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onSetThemeNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        } else if (resId == R.style.RT_AppTheme_Battery) {
            int newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onSetThemeNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        }
        super.setTheme(resId);
    }

    protected void onResumeNightModeChanged(int mode) {

    }

    protected boolean isResumeNightModeChangeEnabled() {
        return true;
    }

    protected void onSetThemeNightModeChanged(int mode) {

    }

    protected boolean isSetThemeNightModeChangeEnabled() {
        return true;
    }

    protected boolean nightModeCondition() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour <= 7 || hour >= 22;
    }

}
