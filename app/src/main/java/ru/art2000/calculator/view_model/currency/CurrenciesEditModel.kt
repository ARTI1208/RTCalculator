package ru.art2000.calculator.view_model.currency

import androidx.lifecycle.LiveData
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.extensions.collections.LiveList

interface CurrenciesEditModel {

    val visibleItems: LiveData<List<CurrencyItem>>

    val selectedVisibleItems: LiveList<CurrencyItem>

    val displayedVisibleItems: LiveList<CurrencyItem>

    var isEditSelectionMode: Boolean

    fun isVisibleItemSelected(item: CurrencyItem): Boolean

    fun setVisibleItemSelected(item: CurrencyItem, selected: Boolean)

    fun databaseMarkHidden(item: CurrencyItem)

    fun dismissFirstTimeTooltip()
}