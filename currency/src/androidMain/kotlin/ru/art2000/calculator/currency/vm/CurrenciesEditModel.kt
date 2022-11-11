package ru.art2000.calculator.currency.vm

import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.extensions.collections.LiveList

internal interface CurrenciesEditModel {

    val visibleItems: Flow<List<CurrencyItem>>

    val selectedVisibleItems: LiveList<CurrencyItem>

    val displayedVisibleItems: LiveList<CurrencyItem>

    var isEditSelectionMode: Boolean

    fun isVisibleItemSelected(item: CurrencyItem): Boolean

    fun setVisibleItemSelected(item: CurrencyItem, selected: Boolean)

    fun databaseMarkHidden(item: CurrencyItem)

    fun dismissFirstTimeTooltip()

    fun swapPositions(item: CurrencyItem, anotherItem: CurrencyItem)
}