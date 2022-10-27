package ru.art2000.calculator

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.HiltAndroidApp
import ru.art2000.calculator.background.currency.CurrencyDownloadWorker
import ru.art2000.helpers.PreferenceHelper
import javax.inject.Inject

@HiltAndroidApp
class CalculatorApplication : MultiDexApplication() {

    @Inject
    lateinit var prefsHelper: PreferenceHelper

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        CurrencyDownloadWorker.setupCurrencyDownload(
            this,
            prefsHelper.currencyBackgroundUpdateType,
            prefsHelper.currencyBackgroundUpdateInterval,
            ExistingPeriodicWorkPolicy.KEEP,
        )

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
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    companion object {
        @JvmField
        val DYNAMIC_COLORS_AVAILABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}