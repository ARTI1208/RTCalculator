package ru.art2000.calculator.main

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import ru.art2000.calculator.R
import ru.art2000.calculator.common.AppStartupListener
import javax.inject.Inject

@HiltAndroidApp
class CalculatorApplication : MultiDexApplication() {

    @Inject
    lateinit var prefsHelper: PreferenceHelper

    @Inject
    lateinit var startupListeners: Set<@JvmSuppressWildcards AppStartupListener>

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startupListeners.forEach { it.onAppStarted(this) }

        checkNightMode(prefsHelper.appTheme)
        prefsHelper.appThemeProperty.listen {
            checkNightMode(it)
        }
    }

    private fun checkNightMode(themeId: Int) {
        val newMode = when (themeId) {
            R.style.RT_AppTheme_DayNight -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
            R.style.RT_AppTheme_System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            R.style.RT_AppTheme_Battery -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            R.style.RT_AppTheme_Dark, R.style.RT_AppTheme_Black -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(newMode)
    }

    companion object {
        @JvmField
        val DYNAMIC_COLORS_AVAILABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}