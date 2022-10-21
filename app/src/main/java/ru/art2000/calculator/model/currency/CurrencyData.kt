package ru.art2000.calculator.model.currency

data class CurrencyData(
    val date: Long,
    val items: List<CurrencyRate>,
)