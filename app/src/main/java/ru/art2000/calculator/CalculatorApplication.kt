package ru.art2000.calculator

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import ru.art2000.helpers.PrefsHelper

class CalculatorApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        PrefsHelper.initialSetup(this)
    }

    companion object {
        @JvmField
        val DYNAMIC_COLORS_AVAILABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}