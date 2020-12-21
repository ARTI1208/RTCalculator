package ru.art2000.calculator.view_model.currency

import androidx.lifecycle.LiveData

interface CurrencyModificationModel {

    val currentQuery: String

    val recyclerViewBottomPadding: LiveData<Int>
}