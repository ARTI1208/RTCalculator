package ru.art2000.extensions.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import ru.art2000.calculator.R
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ru.art2000.helpers.GeneralPreferenceHelper
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
@AndroidEntryPoint
open class DayNightActivity : AppCompatActivity() {

    val generalPrefsHelper: GeneralPreferenceHelper by lazy {
        val holder = EntryPointAccessors.fromApplication<PreferenceHelperHolder>(this)
        holder.prefsHelper
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PreferenceHelperHolder {
        val prefsHelper: GeneralPreferenceHelper
    }

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
            if (generalPrefsHelper.isAppAutoDarkThemeBlack) actualResId = R.style.RT_AppTheme_DayNightBlack
        } else if (actualResId == R.style.RT_AppTheme_System) {
            newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            if (generalPrefsHelper.isAppAutoDarkThemeBlack) actualResId = R.style.RT_AppTheme_SystemBlack
        } else if (actualResId == R.style.RT_AppTheme_Battery) {
            newMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            if (generalPrefsHelper.isAppAutoDarkThemeBlack) actualResId = R.style.RT_AppTheme_BatteryBlack
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
        val startSeconds = generalPrefsHelper.darkThemeActivationTime
        val endSeconds = generalPrefsHelper.darkThemeDeactivationTime
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
        var theme = generalPrefsHelper.appTheme
        if (generalPrefsHelper.areDynamicColorsEnabled) {
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