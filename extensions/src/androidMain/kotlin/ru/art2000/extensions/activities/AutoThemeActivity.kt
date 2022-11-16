package ru.art2000.extensions.activities

import android.os.Bundle
import com.google.android.material.color.DynamicColors
import ru.art2000.extensions.preferences.AppTheme

abstract class AutoThemeActivity : DayNightActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheming()
        super.onCreate(savedInstanceState)
    }

    abstract fun getThemeResId(theme: AppTheme): Int

    private fun applyTheming() {
        if (themeSettingsHolder.areDynamicColorsEnabled) {
            setTheme(getSystemTheme())
            DynamicColors.applyToActivityIfAvailable(this)
        } else {
            setTheme(getThemeResId(themeSettingsHolder.appTheme))
        }
    }
}