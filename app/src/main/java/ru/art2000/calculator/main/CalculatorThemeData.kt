package ru.art2000.calculator.main

import ru.art2000.calculator.R
import ru.art2000.calculator.common.view.AutoThemeData
import javax.inject.Inject

class CalculatorThemeData @Inject constructor() : AutoThemeData {

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

}