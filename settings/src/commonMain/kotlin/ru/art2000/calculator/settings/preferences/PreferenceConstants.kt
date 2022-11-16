package ru.art2000.calculator.settings.preferences

import ru.art2000.extensions.preferences.AppTheme

object PreferenceKeys {
    const val KEY_TAB_DEFAULT = "tab_default" //from settings
    const val KEY_TAB_DEFAULT_OPENED = "default_opened_tab" //currency, calculator or unit
    const val KEY_DYNAMIC_COLORS = "app_dynamic_colors"
    const val KEY_APP_THEME = "app_theme"
    const val KEY_AUTO_DARK_THEME = "app_auto_dark_theme"
    const val KEY_DARK_THEME_ACTIVATION = "app_auto_dark_theme_time_start"
    const val KEY_DARK_THEME_DEACTIVATION = "app_auto_dark_theme_time_end"
}

object PreferenceDefaults {
    val DEFAULT_THEME = AppTheme.SYSTEM
    const val DEFAULT_DYNAMIC_COLORS = true
    const val DEFAULT_TAB = "calc_tab"
    const val DEFAULT_AUTO_DARK_ACTIVATION_TIME = "23:00"
    const val DEFAULT_AUTO_DARK_DEACTIVATION_TIME = "07:00"
    const val DEFAULT_DARK_THEME_IS_BLACK = true
}

object PreferenceValues {
    const val VALUE_TAB_DEFAULT_LAST = "last_tab"
    const val VALUE_TAB_DEFAULT_CALC = "calc_tab"
    const val VALUE_TAB_DEFAULT_CURRENCY = "currency_tab"
    const val VALUE_TAB_DEFAULT_UNIT = "unit_tab"
    const val VALUE_TAB_DEFAULT_SETTINGS = "settings_tab"
    const val VALUE_THEME_LIGHT = "light"
    const val VALUE_THEME_DARK = "dark"
    const val VALUE_THEME_BLACK = "black"
    const val VALUE_THEME_DAY_NIGHT = "day_night"
    const val VALUE_THEME_SYSTEM = "system"
    const val VALUE_THEME_BATTERY = "battery"
}