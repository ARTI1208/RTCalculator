package ru.art2000.calculator.currency.vm

internal interface CurrencyListAdapterModel {

    var lastInputItemPosition: Int

    var lastInputItemValue: Double

    val savedInputItemCode: String

    val savedInputItemValue: Double

    fun saveConversionIfNeeded(code: String)
}