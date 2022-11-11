package ru.art2000.calculator.common.view

import androidx.annotation.StyleRes

interface AutoThemeData {

    @StyleRes
    fun getSystemTheme(black: Boolean): Int

    @StyleRes
    fun getBatteryTheme(black: Boolean): Int

    @StyleRes
    fun getDayNightTheme(black: Boolean): Int

}