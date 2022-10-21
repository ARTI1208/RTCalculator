package ru.art2000.calculator.view_model.currency

import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.extensions.collections.LiveList

interface CurrenciesEditModel {

    val visibleItems: Flow<List<CurrencyItem>>

    val selectedVisibleItems: LiveList<CurrencyItem>

    val displayedVisibleItems: LiveList<CurrencyItem>

    var isEditSelectionMode: Boolean

    fun isVisibleItemSelected(item: CurrencyItem): Boolean

    fun setVisibleItemSelected(item: CurrencyItem, selected: Boolean)

    fun databaseMarkHidden(item: CurrencyItem)

    fun dismissFirstTimeTooltip()
}