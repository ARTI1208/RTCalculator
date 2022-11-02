package ru.art2000.extensions.activities

import android.os.Bundle
import com.google.android.material.color.DynamicColors

abstract class AutoThemeActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheming()
        super.onCreate(savedInstanceState)
    }

    private fun applyTheming() {
        if (themeSettingsHolder.areDynamicColorsEnabled) {
            setTheme(getSystemTheme())
            DynamicColors.applyToActivityIfAvailable(this)
        } else {
            setTheme(themeSettingsHolder.appTheme)
        }
    }
}