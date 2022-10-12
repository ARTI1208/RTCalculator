package ru.art2000.extensions.activities

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.google.android.material.color.DynamicColors
import ru.art2000.calculator.CalculatorApplication
import ru.art2000.calculator.R
import ru.art2000.calculator.view.settings.PreferenceKeys
import ru.art2000.helpers.PrefsHelper

open class AutoThemeActivity : DayNightActivity() {
    private val listener =
        OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PreferenceKeys.KEY_DYNAMIC_COLORS, PreferenceKeys.KEY_APP_THEME -> recreate()
                PreferenceKeys.KEY_AUTO_DARK_THEME -> if (isDarkThemeApplied) {
                    recreate()
                }
                PreferenceKeys.KEY_DARK_THEME_ACTIVATION,
                PreferenceKeys.KEY_DARK_THEME_DEACTIVATION -> requestThemeCheck()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheming()
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun applyTheming() {
        if (CalculatorApplication.DYNAMIC_COLORS_AVAILABLE && PrefsHelper.areDynamicColorsEnabled()) {
            setTheme(R.style.RT_AppTheme_System)
            DynamicColors.applyToActivityIfAvailable(this)
        } else {
            setTheme(PrefsHelper.appTheme)
        }
    }
}