package ru.art2000.extensions.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ru.art2000.extensions.preferences.ThemeSettingsHolder
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
abstract class DayNightActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        if (isResumeNightModeChangeEnabled) {
            requestThemeCheck()
        }
    }

    override fun setTheme(resId: Int) {

        val actualResId = when (resId) {
            getDayNightTheme() -> getDayNightTheme(
                isSetThemeNightModeChangeEnabled && themeSettingsHolder.isBlackThemePreferred
            )
            getSystemTheme() -> getSystemTheme(themeSettingsHolder.isBlackThemePreferred)
            getBatteryTheme() -> getBatteryTheme(themeSettingsHolder.isBlackThemePreferred)
            else -> resId
        }

        super.setTheme(actualResId)
    }

    @Suppress("UNUSED_PARAMETER")
    protected fun onResumeNightModeChanged(@AppCompatDelegate.NightMode mode: Int) {
    }

    protected val isResumeNightModeChangeEnabled: Boolean
        get() = true

    @Suppress("UNUSED_PARAMETER")
    protected fun onSetThemeNightModeChanged(@AppCompatDelegate.NightMode mode: Int) {
    }

    protected val isSetThemeNightModeChangeEnabled: Boolean
        get() = true

    protected abstract val themeSettingsHolder: ThemeSettingsHolder

    protected abstract fun getSystemTheme(black: Boolean = false): Int
    protected abstract fun getBatteryTheme(black: Boolean = false): Int
    protected abstract fun getDayNightTheme(black: Boolean = false): Int

    private fun nightModeCondition(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        val second = calendar[Calendar.SECOND]
        val seconds = (hour * 60 + minute) * 60 + second
        val startSeconds = themeSettingsHolder.darkThemeActivationTime
        val endSeconds = themeSettingsHolder.darkThemeDeactivationTime
        return if (startSeconds < endSeconds) {
            seconds in startSeconds until endSeconds
        } else {
            seconds >= startSeconds || seconds < endSeconds
        }
    }

    protected fun requestThemeCheck() {
        val theme = if (themeSettingsHolder.areDynamicColorsEnabled) {
            getSystemTheme()
        } else {
            themeSettingsHolder.appTheme
        }

        val newMode = when (theme) {
            getDayNightTheme() -> if (nightModeCondition()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            getSystemTheme() -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            getBatteryTheme() -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            else -> return
        }

        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            onResumeNightModeChanged(newMode)
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }
}