package ru.art2000.calculator.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import ru.art2000.calculator.common.AppStartupListener
import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.extensions.activities.EdgeToEdgeActivityCallbacks
import ru.art2000.extensions.preferences.AppTheme
import ru.art2000.extensions.preferences.listen
import javax.inject.Inject

@HiltAndroidApp
class CalculatorApplication : Application() {

    @Inject
    lateinit var prefsHelper: GeneralPreferenceHelper

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

        registerActivityLifecycleCallbacks(EdgeToEdgeActivityCallbacks())
    }

    private fun checkNightMode(theme: AppTheme) {
        val newMode = when (theme) {
            AppTheme.DAY_NIGHT -> AppCompatDelegate.MODE_NIGHT_UNSPECIFIED
            AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            AppTheme.BATTERY -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            AppTheme.DARK, AppTheme.BLACK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(newMode)
    }
}