package ru.art2000.extensions.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.art2000.helpers.PrefsHelper;

public class AutoThemeActivity extends DayNightActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());
        super.onCreate(savedInstanceState);
    }
}
