package ru.art2000.calculator.view_model.currency

interface CurrencyListAdapterModel {

    var lastInputItemPosition: Int

    var lastInputItemValue: Double

    val savedInputItemCode: String

    val savedInputItemValue: Double

    fun saveConversionIfNeeded(code: String)
}