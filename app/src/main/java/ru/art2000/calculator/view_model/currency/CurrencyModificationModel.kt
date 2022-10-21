package ru.art2000.calculator.view_model.currency

import kotlinx.coroutines.flow.StateFlow

interface CurrencyModificationModel {

    val currentQuery: String

    val recyclerViewBottomPadding: StateFlow<Int>
}