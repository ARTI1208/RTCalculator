package ru.art2000.calculator.settings.preferences

import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.extensions.preferences.*
import ru.art2000.extensions.timeStringToSeconds

abstract class CommonPreferenceHelper(preferences: AppPreferences) : GeneralPreferenceHelper {

    final override val appThemeProperty = preferences.enumPreference(
        PreferenceKeys.KEY_APP_THEME, PreferenceDefaults.DEFAULT_THEME,
        object : StringMapping<AppTheme>() {

            override fun String.toOperate() = enumValueOf<AppTheme>(uppercase())

            override fun AppTheme.toStore() = name.lowercase()
        },
    )

    final override var appTheme by appThemeProperty

    private var defaultNavItem by preferences.stringPreference(
        PreferenceKeys.KEY_TAB_DEFAULT_OPENED, PreferenceDefaults.DEFAULT_TAB
    )

    private val defaultTabSettingProperty = preferences.stringPreference(
        PreferenceKeys.KEY_TAB_DEFAULT, PreferenceDefaults.DEFAULT_TAB
    ).listen {
        defaultNavItem = it
    }

    private val isSaveLastEnabled by defaultTabSettingProperty
        .mapGetter<Boolean> { it == PreferenceValues.VALUE_TAB_DEFAULT_LAST }

    final override var defaultTabToOpen: String
        get() = when (val id = defaultNavItem) {
            PreferenceValues.VALUE_TAB_DEFAULT_CALC,
            PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY,
            PreferenceValues.VALUE_TAB_DEFAULT_UNIT,
            PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS -> id
            else -> PreferenceValues.VALUE_TAB_DEFAULT_CALC
        }
        set(value) {
            if (isSaveLastEnabled && value != PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS) {
                val tab = when (value) {
                    PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY,
                    PreferenceValues.VALUE_TAB_DEFAULT_CALC,
                    PreferenceValues.VALUE_TAB_DEFAULT_UNIT -> value
                    else -> throw IndexOutOfBoundsException("Unknown tab id: $value")
                }
                defaultNavItem = tab
            }
        }

    final override val autoDarkThemeProperty: PreferenceDelegate<Boolean> =
        preferences.booleanPreference(
            PreferenceKeys.KEY_AUTO_DARK_THEME, PreferenceDefaults.DEFAULT_DARK_THEME_IS_BLACK
        )

    final override val isBlackThemePreferred by autoDarkThemeProperty

    private val timeMapping = object : PreferenceMapping<Int, String> {

        override fun String.toOperate() = timeStringToSeconds(this)

        override fun Int.toStore() = throw IllegalStateException("Should not be called")
    }

    final override val darkThemeActivationProperty: PreferenceDelegate<Int> =
        preferences.stringPreference(
            PreferenceKeys.KEY_DARK_THEME_ACTIVATION,
            PreferenceDefaults.DEFAULT_AUTO_DARK_ACTIVATION_TIME
        ).mapOperate(timeMapping)

    final override val darkThemeActivationTime by darkThemeActivationProperty

    final override val darkThemeDeactivationProperty: PreferenceDelegate<Int> =
        preferences.stringPreference(
            PreferenceKeys.KEY_DARK_THEME_DEACTIVATION,
            PreferenceDefaults.DEFAULT_AUTO_DARK_DEACTIVATION_TIME
        ).mapOperate(timeMapping)

    final override val darkThemeDeactivationTime by darkThemeDeactivationProperty

}