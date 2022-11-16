package ru.art2000.calculator.common.view

import androidx.annotation.StyleRes
import ru.art2000.extensions.preferences.AppTheme

interface AutoThemeData {

    @StyleRes
    fun getSystemTheme(black: Boolean): Int

    @StyleRes
    fun getBatteryTheme(black: Boolean): Int

    @StyleRes
    fun getDayNightTheme(black: Boolean): Int

    @StyleRes
    fun getThemeRes(theme: AppTheme): Int

}