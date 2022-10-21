package ru.art2000.calculator.view_model.currency

import kotlinx.coroutines.flow.StateFlow
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.extensions.collections.LiveList

interface CurrenciesAddModel : CurrencyModificationModel {

    val hiddenItems: StateFlow<List<CurrencyItem>>

    val selectedHiddenItems: LiveList<CurrencyItem>

    val displayedHiddenItems: LiveList<CurrencyItem>

    fun isHiddenItemSelected(item: CurrencyItem): Boolean

    fun setHiddenItemSelected(item: CurrencyItem, selected: Boolean)
}