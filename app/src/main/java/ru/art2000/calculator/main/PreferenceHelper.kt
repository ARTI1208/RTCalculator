package ru.art2000.calculator.main

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.art2000.calculator.R
import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.calculator.settings.PreferenceDefaults
import ru.art2000.calculator.settings.PreferenceKeys
import ru.art2000.calculator.settings.PreferenceValues
import ru.art2000.extensions.preferences.*
import ru.art2000.extensions.timeStringToSeconds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : GeneralPreferenceHelper {

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

    override val dynamicColorsProperty: ReadOnlyPreferenceDelegate<Boolean> =
        preferences.booleanPreference(
            PreferenceKeys.KEY_DYNAMIC_COLORS, PreferenceDefaults.DEFAULT_DYNAMIC_COLORS
        ).mapGetter {
            CalculatorApplication.DYNAMIC_COLORS_AVAILABLE && it
        }

    override val areDynamicColorsEnabled by dynamicColorsProperty

    override val appThemeProperty: ReadOnlyPreferenceDelegate<Int> = preferences.stringPreference(
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

    override val appTheme by appThemeProperty

    override val autoDarkThemeProperty: ReadOnlyPreferenceDelegate<Boolean> =
        preferences.booleanPreference(
            PreferenceKeys.KEY_AUTO_DARK_THEME, PreferenceDefaults.DEFAULT_DARK_THEME_IS_BLACK
        )

    override val isBlackThemePreferred by autoDarkThemeProperty

    private val timeMapping = object : OperateMapping<Int, String> {
        override fun toOperate(value: String) = timeStringToSeconds(value)

        override fun toStore(value: Int) = throw IllegalStateException("Should not be called")
    }

    override val darkThemeActivationProperty: ReadOnlyPreferenceDelegate<Int> =
        preferences.stringPreference(
            PreferenceKeys.KEY_DARK_THEME_ACTIVATION,
            PreferenceDefaults.DEFAULT_AUTO_DARK_ACTIVATION_TIME
        ).mapOperate(timeMapping)

    override val darkThemeActivationTime by darkThemeActivationProperty

    override val darkThemeDeactivationProperty: ReadOnlyPreferenceDelegate<Int> =
        preferences.stringPreference(
            PreferenceKeys.KEY_DARK_THEME_DEACTIVATION,
            PreferenceDefaults.DEFAULT_AUTO_DARK_DEACTIVATION_TIME
        ).mapOperate(timeMapping)

    override val darkThemeDeactivationTime by darkThemeDeactivationProperty
}