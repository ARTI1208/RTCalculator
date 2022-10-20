package ru.art2000.helpers

import ru.art2000.calculator.R

object PreferenceKeys {
    const val KEY_TAB_DEFAULT = "tab_default" //from settings
    const val KEY_TAB_DEFAULT_OPENED = "default_opened_tab" //currency, calculator or unit
    const val KEY_DYNAMIC_COLORS = "app_dynamic_colors"
    const val KEY_APP_THEME = "app_theme"
    const val KEY_AUTO_DARK_THEME = "app_auto_dark_theme"
    const val KEY_DARK_THEME_ACTIVATION = "app_auto_dark_theme_time_start"
    const val KEY_DARK_THEME_DEACTIVATION = "app_auto_dark_theme_time_end"
    const val KEY_SAVE_CURRENCY = "save_currency_value"
    const val KEY_ZERO_DIVISION = "zero_div"
    const val KEY_UNIT_VIEW = "unit_view"
    const val KEY_CURRENCIES_BACKGROUND = "update_currencies_in_background"
    const val KEY_CURRENCIES_INTERVAL = "currency_update_interval"
    const val KEY_CURRENCY_DELETE_TOOLTIP_SHOWN = "delete_tooltip_shown"
    const val KEY_CURRENCY_UPDATE_ON_TAB_OPEN = "update_currencies_on_tab_open"
    const val KEY_CURRENCY_UPDATE_DATE = "currency_update_date"
}

object PreferenceDefaults {
    const val DEFAULT_THEME = "system"
    const val DEFAULT_DYNAMIC_COLORS = true
    const val DEFAULT_TAB = "calc_tab"
    const val DEFAULT_UNIT_VIEW = "powerful"
    const val DEFAULT_ZERO_DIVISION = true
    const val DEFAULT_CONVERSION_CODE = "USD"
    const val DEFAULT_AUTO_DARK_ACTIVATION_TIME = "23:00"
    const val DEFAULT_AUTO_DARK_DEACTIVATION_TIME = "07:00"
    const val DEFAULT_DARK_THEME_IS_BLACK = true
    const val DEFAULT_CONVERSION_VALUE = 1.0
    const val DEFAULT_SHOULD_SAVE_CONVERSION_VALUE = false
    const val DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE =
        PreferenceValues.VALUE_CURRENCY_BACKGROUND_NO_UPDATE
    const val DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL = 8
    const val DEFAULT_CURRENCY_DELETE_TOOLTIP_SHOWN = false
    const val DEFAULT_CURRENCY_UPDATE_ON_TAB_OPEN = true
    const val DEFAULT_CURRENCY_UPDATE_DATE = R.string.preloaded_currencies_date
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
    const val VALUE_CURRENCY_BACKGROUND_NO_UPDATE = "no_update"
    const val VALUE_CURRENCY_BACKGROUND_WIFI = "wifi"
    const val VALUE_CURRENCY_BACKGROUND_ANY = "any"
}