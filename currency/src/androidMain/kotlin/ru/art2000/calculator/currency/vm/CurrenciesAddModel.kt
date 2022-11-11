package ru.art2000.calculator.currency.vm

import kotlinx.coroutines.flow.StateFlow
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.extensions.collections.LiveList

internal interface CurrenciesAddModel : CurrencyModificationModel {

    val hiddenItems: StateFlow<List<CurrencyItem>>

    val selectedHiddenItems: LiveList<CurrencyItem>

    val displayedHiddenItems: LiveList<CurrencyItem>

    fun isHiddenItemSelected(item: CurrencyItem): Boolean

    fun setHiddenItemSelected(item: CurrencyItem, selected: Boolean)
}