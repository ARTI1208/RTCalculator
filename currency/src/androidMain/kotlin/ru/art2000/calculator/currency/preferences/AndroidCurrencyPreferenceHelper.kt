package ru.art2000.calculator.currency.preferences

import ru.art2000.extensions.preferences.ReadOnlyPreferenceDelegate

internal interface AndroidCurrencyPreferenceHelper : CurrencyPreferenceHelper {

    val updateDateMillisProperty: ReadOnlyPreferenceDelegate<Long>

}