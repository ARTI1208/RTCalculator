package ru.art2000.calculator

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.HiltAndroidApp
import ru.art2000.calculator.background.currency.CurrencyFunctions
import ru.art2000.helpers.PreferenceHelper
import javax.inject.Inject

@HiltAndroidApp
class CalculatorApplication : MultiDexApplication() {

    @Inject
    lateinit var prefsHelper: PreferenceHelper

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        CurrencyFunctions.setupCurrencyDownload(
            this,
            prefsHelper.currencyBackgroundUpdateType,
            prefsHelper.currencyBackgroundUpdateInterval,
            ExistingPeriodicWorkPolicy.KEEP,
        )
    }

    companion object {
        @JvmField
        val DYNAMIC_COLORS_AVAILABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}