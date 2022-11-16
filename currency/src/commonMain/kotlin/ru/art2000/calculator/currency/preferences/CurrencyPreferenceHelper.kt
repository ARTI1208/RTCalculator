package ru.art2000.calculator.currency.preferences

import ru.art2000.extensions.preferences.PreferenceDelegate

internal interface CurrencyPreferenceHelper {

    val updateOnFirstTabOpen: Boolean

    val updateDateMillisProperty: PreferenceDelegate<Long>

    var updateDateMillis: Long

    val conversionCode: String

    val conversionValue: Double

    fun putConversionValuesIfNeeded(code: String, value: Double)


    val currencyBackgroundUpdateType: String

    val currencyBackgroundUpdateInterval: Int


    val isDeleteTooltipShown: Boolean

    fun setDeleteTooltipShown()

}