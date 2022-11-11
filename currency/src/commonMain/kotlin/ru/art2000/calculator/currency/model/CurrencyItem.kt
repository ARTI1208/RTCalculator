package ru.art2000.calculator.currency.model

internal data class CurrencyItem(
    val code: String,
    val rate: Double,
    val position: Int,
)
