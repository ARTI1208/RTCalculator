package ru.art2000.calculator.currency.preferences

internal object CurrencyKeys {
    const val KEY_SAVE_CURRENCY = "save_currency_value"
    const val KEY_CURRENCIES_BACKGROUND = "update_currencies_in_background"
    const val KEY_CURRENCIES_INTERVAL = "currency_update_interval"
    const val KEY_CURRENCY_DELETE_TOOLTIP_SHOWN = "delete_tooltip_shown"
    const val KEY_CURRENCY_UPDATE_ON_TAB_OPEN = "update_currencies_on_tab_open"
    const val KEY_CURRENCY_UPDATE_DATE_MILLIS = "currency_update_date_millis"
}

internal object CurrencyDefaults {
    const val DEFAULT_CONVERSION_CODE = "USD"
    const val DEFAULT_CONVERSION_VALUE = 1.0
    const val DEFAULT_SHOULD_SAVE_CONVERSION_VALUE = false
    const val DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE =
        CurrencyValues.VALUE_CURRENCY_BACKGROUND_NO_UPDATE
    const val DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL = 8
    const val DEFAULT_CURRENCY_DELETE_TOOLTIP_SHOWN = false
    const val DEFAULT_CURRENCY_UPDATE_ON_TAB_OPEN = true
    const val DEFAULT_CURRENCY_PRELOADED_UPDATE_DATE = 1609459200000
}

internal object CurrencyValues {
    const val VALUE_CURRENCY_BACKGROUND_NO_UPDATE = "no_update"
    const val VALUE_CURRENCY_BACKGROUND_WIFI = "wifi"
    const val VALUE_CURRENCY_BACKGROUND_ANY = "any"
}