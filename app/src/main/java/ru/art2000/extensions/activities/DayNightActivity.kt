package ru.art2000.extensions.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import ru.art2000.calculator.R
import androidx.appcompat.app.AppCompatDelegate
import ru.art2000.helpers.PrefsHelper
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
open class DayNightActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        if (isResumeNightModeChangeEnabled) {
            requestThemeCheck()
        }
    }

    override fun setTheme(resId: Int) {
        var actualResId = resId
        val newMode: Int
        if (actualResId == R.style.RT_AppTheme_DayNight && isSetThemeNightModeChangeEnabled) {
            newMode = if (nightModeCondition()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            if (PrefsHelper.isAppAutoDarkThemeBlack) actualResId = R.style.RT_AppTheme_DayNightBlack
        } else if (actualResId == R.style.RT_AppTheme_System) {
            newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            if (PrefsHelper.isAppAutoDarkThemeBlack) actualResId = R.style.RT_AppTheme_SystemBlack
        } else if (actualResId == R.style.RT_AppTheme_Battery) {
            newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            if (PrefsHelper.isAppAutoDarkThemeBlack) actualResId = R.style.RT_AppTheme_BatteryBlack
        } else if (actualResId == R.style.RT_AppTheme_Dark || actualResId == R.style.RT_AppTheme_Black) {
            newMode = AppCompatDelegate.MODE_NIGHT_YES
        } else {
            newMode = AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            onSetThemeNightModeChanged(newMode)
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
        super.setTheme(actualResId)
    }

    @Suppress("UNUSED_PARAMETER")
    protected fun onResumeNightModeChanged(@AppCompatDelegate.NightMode mode: Int) {}
    protected val isResumeNightModeChangeEnabled: Boolean
        get() = true

    @Suppress("UNUSED_PARAMETER")
    protected fun onSetThemeNightModeChanged(@AppCompatDelegate.NightMode mode: Int) {}
    protected val isSetThemeNightModeChangeEnabled: Boolean
        get() = true

    private fun nightModeCondition(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        val seconds = (hour * 60 + minute) * 60 + second
        val startSeconds = PrefsHelper.darkThemeActivationTime
        val endSeconds = PrefsHelper.darkThemeDeactivationTime
        return if (startSeconds < endSeconds) {
            seconds in startSeconds until endSeconds
        } else {
            seconds >= startSeconds || seconds < endSeconds
        }
    }

    val isDarkThemeApplied: Boolean
        get() {
            val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        }

    protected fun requestThemeCheck() {
        var theme = PrefsHelper.appTheme
        if (PrefsHelper.areDynamicColorsEnabled()) {
            theme = R.style.RT_AppTheme_System
        }
        if (theme == R.style.RT_AppTheme_DayNight) {
            val newMode = if (nightModeCondition()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onResumeNightModeChanged(newMode)
                AppCompatDelegate.setDefaultNightMode(newMode)
            }
        } else if (theme == R.style.RT_AppTheme_System) {
            val newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onResumeNightModeChanged(newMode)
                AppCompatDelegate.setDefaultNightMode(newMode)
            }
        } else if (theme == R.style.RT_AppTheme_Battery) {
            val newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                onResumeNightModeChanged(newMode)
                AppCompatDelegate.setDefaultNightMode(newMode)
            }
        }
    }
}