package ru.art2000.calculator.currency.preferences

internal interface CurrencyPreferenceHelper {

    val updateOnFirstTabOpen: Boolean

    var updateDateMillis: Long

    val conversionCode: String

    val conversionValue: Double

    fun putConversionValuesIfNeeded(code: String, value: Double)


    val currencyBackgroundUpdateType: String

    val currencyBackgroundUpdateInterval: Int


    val isDeleteTooltipShown: Boolean

    fun setDeleteTooltipShown()

}