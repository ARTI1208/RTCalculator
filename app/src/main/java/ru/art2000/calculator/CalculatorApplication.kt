package ru.art2000.calculator

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import ru.art2000.helpers.PrefsHelper

class CalculatorApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

}