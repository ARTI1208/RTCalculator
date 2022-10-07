package ru.art2000.extensions.activities;

import android.annotation.SuppressLint;
import android.content.res.Configuration;

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
        if (isResumeNightModeChangeEnabled()) {
            requestThemeCheck();
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
            if (PrefsHelper.isAppAutoDarkThemeBlack())
                resId = R.style.RT_AppTheme_DayNightBlack;
        } else if (resId == R.style.RT_AppTheme_System) {
            newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            if (PrefsHelper.isAppAutoDarkThemeBlack())
                resId = R.style.RT_AppTheme_SystemBlack;
        } else if (resId == R.style.RT_AppTheme_Battery) {
            newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
            if (PrefsHelper.isAppAutoDarkThemeBlack())
                resId = R.style.RT_AppTheme_BatteryBlack;
        } else if (resId == R.style.RT_AppTheme_Dark || resId == R.style.RT_AppTheme_Black) {
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

    public boolean nightModeCondition() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        int seconds = (hour * 60 + minute) * 60 + second;
        int startSeconds = PrefsHelper.getDarkThemeActivationTime();
        int endSeconds = PrefsHelper.getDarkThemeDeactivationTime();

        if (startSeconds < endSeconds) {
            return seconds >= startSeconds && seconds < endSeconds;
        } else {
            return seconds >= startSeconds || seconds < endSeconds;
        }
    }

    public boolean isDarkThemeApplied() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    protected void requestThemeCheck() {
        int theme = PrefsHelper.getAppTheme();
        if (PrefsHelper.areDynamicColorsEnabled()) {
            theme = R.style.RT_AppTheme_System;
        }

        if (theme == R.style.RT_AppTheme_DayNight) {
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
}
