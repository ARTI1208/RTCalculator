package ru.art2000.calculator.currency.vm

import kotlinx.coroutines.flow.StateFlow

internal interface CurrencyModificationModel {

    val currentQuery: String

    val recyclerViewBottomPadding: StateFlow<Int>
}