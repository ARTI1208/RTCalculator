package ru.art2000.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.art2000.calculator.CalculatorApplication
import ru.art2000.calculator.R
import ru.art2000.extensions.preferences.TimePickerPreference.Companion.parseStringTime

object PrefsHelper {
    private const val DEFAULT_THEME = "system"
    private const val DEFAULT_TAB = "calc_tab"
    private const val DEFAULT_UNIT_VIEW = "powerful"
    private const val DEFAULT_CONVERSION_CODE = "USD"
    private const val DEFAULT_AUTO_DARK_ACTIVATION_TIME = "23:00"
    private const val DEFAULT_AUTO_DARK_DEACTIVATION_TIME = "07:00"
    private const val DEFAULT_DARK_THEME_IS_BLACK = true
    private const val DEFAULT_CONVERSION_VALUE = 1.0
    private const val DEFAULT_SHOULD_SAVE_CONVERSION_VALUE = false
    private const val DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE = "no_update"
    private const val DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL = 8

    private var sSharedPreferences: SharedPreferences? = null

    var appTheme = 0
        private set

    var isAppAutoDarkThemeBlack = false
        private set

    var defaultNavItem = 0
        private set

    private var sShouldSaveCurrencyConversion = false

    private var sUnitViewType: String? = null

    var conversionCode: String? = null
        private set

    var conversionValue = 0.0
        private set

    var darkThemeActivationTime = 0
        private set

    var darkThemeDeactivationTime = 0
        private set

    var isShouldSaveCurrencyConversion: Boolean
        get() = sShouldSaveCurrencyConversion
        set(value) {
            sShouldSaveCurrencyConversion = value
            sSharedPreferences!!.edit().putBoolean("save_currency_value", value).apply()
            if (!value) sSharedPreferences!!.edit()
                .remove("last_conversion_code")
                .remove("last_conversion_double")
                .apply()
        }

    fun putConversionValues(from: String?, value: Double) {
        sSharedPreferences!!.edit().putString("last_conversion_code", from).apply()
        putConversionDouble(value)
    }

    private fun putConversionDouble(value: Double) {
        putDouble("last_conversion_double", value)
    }

    private fun putDouble(key: String, value: Double) {
        sSharedPreferences!!.edit().putLong(key, java.lang.Double.doubleToRawLongBits(value))
            .apply()
    }

    private fun getDouble(key: String, defaultValue: Double): Double {
        return java.lang.Double.longBitsToDouble(
            sSharedPreferences!!.getLong(key, java.lang.Double.doubleToLongBits(defaultValue))
        )
    }

    fun initialSetup(mContext: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        sSharedPreferences = prefs

        appTheme = when (prefs.getString("app_theme", DEFAULT_THEME)) {
            "light" -> R.style.RT_AppTheme_Light
            "dark" -> R.style.RT_AppTheme_Dark
            "black" -> R.style.RT_AppTheme_Black
            "day_night" -> R.style.RT_AppTheme_DayNight
            "system" -> R.style.RT_AppTheme_System
            "battery" -> R.style.RT_AppTheme_Battery
            else -> R.style.RT_AppTheme_Light
        }
        isAppAutoDarkThemeBlack =
            prefs.getBoolean("app_auto_dark_theme", DEFAULT_DARK_THEME_IS_BLACK)
        sShouldSaveCurrencyConversion = prefs.getBoolean(
            "save_currency_value",
            DEFAULT_SHOULD_SAVE_CONVERSION_VALUE
        )
        conversionCode =
            prefs.getString("last_conversion_code", DEFAULT_CONVERSION_CODE)
        conversionValue = getDouble("last_conversion_double", DEFAULT_CONVERSION_VALUE)
        defaultNavItem = when (prefs.getString("default_opened_tab", DEFAULT_TAB)) {
            "calc_tab" -> R.id.navigation_calc
            "currency_tab" -> R.id.navigation_currency
            "unit_tab" -> R.id.navigation_unit
            "settings_tab" -> R.id.navigation_settings
            else -> R.id.navigation_calc
        }
        sUnitViewType = prefs.getString("unit_view", DEFAULT_UNIT_VIEW)
        val activationTimeString = prefs.getString(
            "app_auto_dark_theme_time_start", DEFAULT_AUTO_DARK_ACTIVATION_TIME
        )
        val deactivationTimeString = prefs.getString(
            "app_auto_dark_theme_time_end", DEFAULT_AUTO_DARK_DEACTIVATION_TIME
        )
        darkThemeActivationTime = timeStringToSeconds(activationTimeString)
        darkThemeDeactivationTime = timeStringToSeconds(deactivationTimeString)
    }

    private fun timeStringToSeconds(time: String?): Int {
        val (first, second) = parseStringTime(
            time!!
        )
        return (first * 60 + second) * 60
    }

    var unitViewType: String
        get() = sUnitViewType!!
        set(view) {
            sUnitViewType = view
            sSharedPreferences!!.edit().putString("unit_view", view).apply()
        }

    fun setDefaultTab(pos: Int) {
        if (isSaveLastEnabled && pos != 3) {
            val tab = when (pos) {
                0 -> "currency_tab"
                1 -> "calc_tab"
                2 -> "unit_tab"
                3 -> "settings_tab"
                else -> throw IndexOutOfBoundsException("Tab index must be between 0 and 3, inclusive")
            }
            sSharedPreferences!!.edit().putString("default_opened_tab", tab).apply()
        }
    }

    fun setDefaultTab(tab: String?) {
        sSharedPreferences!!.edit().putString("default_opened_tab", tab).apply()
    }

    fun setAppTheme(theme: String?) {
        sSharedPreferences!!.edit().putString("app_theme", theme).apply()
    }

    fun setAppAutoDarkThemeIsBlack(isBlack: Boolean) {
        sSharedPreferences!!.edit().putBoolean("app_auto_dark_theme", isBlack).apply()
    }

    fun setDarkThemeActivationTime(time: String?) {
        darkThemeActivationTime = timeStringToSeconds(time)
    }

    fun setDarkThemeDeactivationTime(time: String?) {
        darkThemeDeactivationTime = timeStringToSeconds(time)
    }

    val zeroDivResult: Int
        get() = if (sSharedPreferences!!.getBoolean(
                "zero_div",
                true
            )
        ) R.string.infinity else R.string.error

    fun setZeroDivResult(b: Boolean) {
        sSharedPreferences!!.edit().putBoolean("zero_div", b).apply()
    }

    private val isSaveLastEnabled: Boolean
        get() = sSharedPreferences!!.getString("tab_default", "") == "last_tab"

    fun setDeleteTooltipShown() {
        sSharedPreferences!!.edit().putBoolean("delete_tooltip_shown", true).apply()
    }

    val isDeleteTooltipShown: Boolean
        get() = sSharedPreferences!!.getBoolean("delete_tooltip_shown", false)
    var currencyBackgroundUpdateType: String
        get() = sSharedPreferences!!.getString(
            "update_currencies_in_background",
            DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE
        )!!
        set(type) {
            sSharedPreferences!!.edit().putString(
                "update_currencies_in_background",
                type
            ).apply()
        }

    private fun stringIntPref(key: String, defaultValue: Int): Int {
        val stringValue = sSharedPreferences!!.getString(
            key,
            null
        ) ?: return defaultValue
        return stringValue.toInt()
    }

    var currencyBackgroundUpdateInterval: Int
        get() = stringIntPref(
            "currency_update_interval",
            DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL
        )
        set(interval) {
            sSharedPreferences!!.edit().putString(
                "currency_update_interval", interval.toString()
            ).apply()
        }

    fun areDynamicColorsEnabled(): Boolean {
        return CalculatorApplication.DYNAMIC_COLORS_AVAILABLE && sSharedPreferences!!.getBoolean(
            "app_dynamic_colors",
            true
        )
    }

    fun setDynamicColorsEnabled(enabled: Boolean) {
        sSharedPreferences!!.edit().putBoolean("app_dynamic_colors", enabled).apply()
    }
}