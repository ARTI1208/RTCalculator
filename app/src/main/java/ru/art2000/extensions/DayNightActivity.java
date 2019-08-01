package ru.art2000.extensions;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

import ru.art2000.calculator.R;
import ru.art2000.calculator.settings.PrefsHelper;

@SuppressLint("Registered")
public class DayNightActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        int theme = PrefsHelper.getAppTheme();
        if (theme == R.style.AppTheme_DayNight) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

            int newMode;

            if (hour <= 7 || hour >= 12) {
                newMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                newMode = AppCompatDelegate.MODE_NIGHT_NO;
            }

            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onPreNightModeChanged(newMode);
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        }
    }

    protected void onPreNightModeChanged(int mode) {

    }
}
