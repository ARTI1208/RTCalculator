package ru.art2000.calculator.view

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ru.art2000.calculator.R
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.activities.IEdgeToEdgeActivity
import ru.art2000.extensions.preferences.ThemeSettingsHolder
import ru.art2000.extensions.views.isDarkThemeApplied
import ru.art2000.helpers.GeneralPreferenceHelper
import ru.art2000.helpers.PreferenceKeys

abstract class AppActivity : AutoThemeActivity(), IEdgeToEdgeActivity {
    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                PreferenceKeys.KEY_DYNAMIC_COLORS, PreferenceKeys.KEY_APP_THEME ->
                    recreate()
                PreferenceKeys.KEY_AUTO_DARK_THEME -> if (isDarkThemeApplied) {
                    recreate()
                }
                PreferenceKeys.KEY_DARK_THEME_ACTIVATION,
                PreferenceKeys.KEY_DARK_THEME_DEACTIVATION -> requestThemeCheck()
            }
        }

    protected val generalPrefsHelper: GeneralPreferenceHelper by lazy {
        val holder = EntryPointAccessors.fromApplication<PreferenceHelperHolder>(this)
        holder.prefsHelper
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PreferenceHelperHolder {
        val prefsHelper: GeneralPreferenceHelper
    }

    override val themeSettingsHolder: ThemeSettingsHolder
        get() = generalPrefsHelper

    override fun getSystemTheme(black: Boolean) = when (black) {
        false -> R.style.RT_AppTheme_System
        true -> R.style.RT_AppTheme_SystemBlack
    }

    override fun getBatteryTheme(black: Boolean) = when (black) {
        false -> R.style.RT_AppTheme_Battery
        true -> R.style.RT_AppTheme_BatteryBlack
    }

    override fun getDayNightTheme(black: Boolean) = when (black) {
        false -> R.style.RT_AppTheme_DayNight
        true -> R.style.RT_AppTheme_DayNightBlack
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        applyEdgeToEdgeIfAvailable()
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(listener)
    }

}