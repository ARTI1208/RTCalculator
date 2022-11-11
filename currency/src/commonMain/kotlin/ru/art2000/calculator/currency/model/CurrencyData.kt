package ru.art2000.calculator.currency.model

import kotlinx.datetime.LocalDate

internal data class CurrencyData(
    val date: LocalDate,
    val items: List<CurrencyRate>,
)