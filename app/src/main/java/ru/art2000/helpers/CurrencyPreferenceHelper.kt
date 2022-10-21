package ru.art2000.helpers

import ru.art2000.extensions.preferences.ReadOnlyPreferenceDelegate

interface CurrencyPreferenceHelper {

    val updateOnFirstTabOpen: Boolean

    val updateDateMillisProperty: ReadOnlyPreferenceDelegate<Long>

    var updateDateMillis: Long

    val conversionCode: String

    val conversionValue: Double

    fun putConversionValuesIfNeeded(code: String, value: Double)


    val currencyBackgroundUpdateType: String

    val currencyBackgroundUpdateInterval: Int


    val isDeleteTooltipShown: Boolean

    fun setDeleteTooltipShown()

}