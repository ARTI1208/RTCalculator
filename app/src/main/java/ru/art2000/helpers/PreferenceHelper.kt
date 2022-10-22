package ru.art2000.helpers

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.art2000.calculator.CalculatorApplication
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyDownloadWorker
import ru.art2000.extensions.preferences.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : GeneralPreferenceHelper, CurrencyPreferenceHelper,
    CalculatorPreferenceHelper, UnitPreferenceHelper {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    // General =========================================================

    private var defaultNavItem by preferences.stringPreference(
        PreferenceKeys.KEY_TAB_DEFAULT_OPENED, PreferenceDefaults.DEFAULT_TAB
    )

    private val isSaveLastEnabled by preferences.stringPreference(
        PreferenceKeys.KEY_TAB_DEFAULT, PreferenceDefaults.DEFAULT_TAB
    )
        .listen { defaultNavItem = it }
        .getAs { it == PreferenceValues.VALUE_TAB_DEFAULT_LAST }

    override var defaultNavItemId: Int
        get() = when (defaultNavItem) {
            PreferenceValues.VALUE_TAB_DEFAULT_CALC -> R.id.navigation_calc
            PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY -> R.id.navigation_currency
            PreferenceValues.VALUE_TAB_DEFAULT_UNIT -> R.id.navigation_unit
            PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS -> R.id.navigation_settings
            else -> R.id.navigation_calc
        }
        set(value) {
            if (isSaveLastEnabled && value != R.id.navigation_settings) {
                val tab = when (value) {
                    R.id.navigation_currency -> PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY
                    R.id.navigation_calc -> PreferenceValues.VALUE_TAB_DEFAULT_CALC
                    R.id.navigation_unit -> PreferenceValues.VALUE_TAB_DEFAULT_UNIT
                    else -> throw IndexOutOfBoundsException("Unknown tab id: $value")
                }
                defaultNavItem = tab
            }
        }

    override val areDynamicColorsEnabled by preferences.booleanPreference(
        PreferenceKeys.KEY_DYNAMIC_COLORS, PreferenceDefaults.DEFAULT_DYNAMIC_COLORS
    ).mapGetter {
        CalculatorApplication.DYNAMIC_COLORS_AVAILABLE && it
    }

    override val appTheme by preferences.stringPreference(
        PreferenceKeys.KEY_APP_THEME, PreferenceDefaults.DEFAULT_THEME
    ).getAs {
        when (it) {
            PreferenceValues.VALUE_THEME_LIGHT -> R.style.RT_AppTheme_Light
            PreferenceValues.VALUE_THEME_DARK -> R.style.RT_AppTheme_Dark
            PreferenceValues.VALUE_THEME_BLACK -> R.style.RT_AppTheme_Black
            PreferenceValues.VALUE_THEME_DAY_NIGHT -> R.style.RT_AppTheme_DayNight
            PreferenceValues.VALUE_THEME_SYSTEM -> R.style.RT_AppTheme_System
            PreferenceValues.VALUE_THEME_BATTERY -> R.style.RT_AppTheme_Battery
            else -> R.style.RT_AppTheme_Light
        }
    }

    override val isAppAutoDarkThemeBlack by preferences.booleanPreference(
        PreferenceKeys.KEY_AUTO_DARK_THEME, PreferenceDefaults.DEFAULT_DARK_THEME_IS_BLACK
    )

    private val timeMapping = object : OperateMapping<Int, String> {
        override fun toOperate(value: String) = timeStringToSeconds(value)

        override fun toStore(value: Int) = throw IllegalStateException("Should not be called")
    }

    override val darkThemeActivationTime by preferences.stringPreference(
        PreferenceKeys.KEY_DARK_THEME_ACTIVATION,
        PreferenceDefaults.DEFAULT_AUTO_DARK_ACTIVATION_TIME
    ).mapOperate(timeMapping)

    override val darkThemeDeactivationTime by preferences.stringPreference(
        PreferenceKeys.KEY_DARK_THEME_DEACTIVATION,
        PreferenceDefaults.DEFAULT_AUTO_DARK_DEACTIVATION_TIME
    ).mapOperate(timeMapping)


    // Calculator ========================================================

    override val zeroDivResult by preferences.booleanPreference(
        PreferenceKeys.KEY_ZERO_DIVISION, PreferenceDefaults.DEFAULT_ZERO_DIVISION
    ).getAs { if (it) R.string.infinity else R.string.error }

    override var lastExpression by preferences.nullableStringPreference(
        "lastExpression"
    )

    override var lastExpressionWasCalculated by preferences.booleanPreference(
        "lastExpressionWasCalculated", false
    )
    override var lastMemory by preferences.doublePreference(
        "lastMemory", 0.0
    )


    // Currency ==========================================================

    private val isShouldSaveCurrencyConversion by preferences.booleanPreference(
        PreferenceKeys.KEY_SAVE_CURRENCY, PreferenceDefaults.DEFAULT_SHOULD_SAVE_CONVERSION_VALUE
    ).listen {
        if (!it) {
            clearConversionValues()
        }
    }
    override val updateOnFirstTabOpen by preferences.booleanPreference(
        PreferenceKeys.KEY_CURRENCY_UPDATE_ON_TAB_OPEN,
        PreferenceDefaults.DEFAULT_CURRENCY_UPDATE_ON_TAB_OPEN
    )

    override val updateDateMillisProperty = preferences.longPreference(
        PreferenceKeys.KEY_CURRENCY_UPDATE_DATE_MILLIS,
        context.getString(PreferenceDefaults.DEFAULT_CURRENCY_UPDATE_DATE).toLong()
    )

    override var updateDateMillis by updateDateMillisProperty

    override var conversionCode by preferences.stringPreference(
        "last_conversion_code", PreferenceDefaults.DEFAULT_CONVERSION_CODE
    )
        private set

    override var conversionValue by preferences.doublePreference(
        "last_conversion_double", PreferenceDefaults.DEFAULT_CONVERSION_VALUE
    ).mapGetter {
        if (isShouldSaveCurrencyConversion) it else PreferenceDefaults.DEFAULT_CONVERSION_VALUE
    }
        private set

    override fun putConversionValuesIfNeeded(code: String, value: Double) {
        if (!isShouldSaveCurrencyConversion) return

        conversionCode = code
        conversionValue = value
    }

    private fun clearConversionValues() {
        conversionCode = PreferenceDefaults.DEFAULT_CONVERSION_CODE
        conversionValue = PreferenceDefaults.DEFAULT_CONVERSION_VALUE
    }

    private fun setCurrencyUpdateTypeInterval(type: String, interval: Int) =
        CurrencyDownloadWorker.setupCurrencyDownload(
            context, type, interval, ExistingPeriodicWorkPolicy.REPLACE,
        )

    override val currencyBackgroundUpdateType by preferences.stringPreference(
        PreferenceKeys.KEY_CURRENCIES_BACKGROUND,
        PreferenceDefaults.DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE
    ).listen { type -> setCurrencyUpdateTypeInterval(type, currencyBackgroundUpdateInterval) }

    override val currencyBackgroundUpdateInterval: Int by preferences
        .intPreference(
            PreferenceKeys.KEY_CURRENCIES_INTERVAL,
            PreferenceDefaults.DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL
        ).mapStore(IntStringMapping)
        .listen { interval ->
            setCurrencyUpdateTypeInterval(currencyBackgroundUpdateType, interval)
        }

    override var isDeleteTooltipShown by preferences.booleanPreference(
        PreferenceKeys.KEY_CURRENCY_DELETE_TOOLTIP_SHOWN,
        PreferenceDefaults.DEFAULT_CURRENCY_DELETE_TOOLTIP_SHOWN
    )
        private set

    override fun setDeleteTooltipShown() {
        isDeleteTooltipShown = true
    }


    // Unit ============================================================

    private val unitViewTypeProperty = preferences.stringPreference(
        PreferenceKeys.KEY_UNIT_VIEW,
        PreferenceDefaults.DEFAULT_UNIT_VIEW
    )

    override val unitViewType by unitViewTypeProperty

    override fun setOnViewTypeChanged(onChanged: ((String) -> Unit)?) {
        if (onChanged != null) {
            unitViewTypeProperty.listen(onChanged)
        } else {
            unitViewTypeProperty.stopListening()
        }
    }


    // Other ===========================================================

    companion object {

        @JvmStatic
        private fun timeStringToSeconds(time: String): Int {
            val (first, second) = parseStringTime(time)
            return (first * 60 + second) * 60
        }

        @JvmStatic
        fun parseStringTime(time: String): Pair<Int, Int> {
            val parts = time.split(':')
            val e = IllegalArgumentException("String '$time' is not time string")
            if (parts.size != 2) throw e

            val hour = (parts.first().toIntOrNull() ?: throw e)
                .coerceAtLeast(0)
                .coerceAtMost(23)

            val minute = (parts.last().toIntOrNull() ?: throw e)
                .coerceAtLeast(0)
                .coerceAtMost(59)

            return hour to minute
        }
    }
}