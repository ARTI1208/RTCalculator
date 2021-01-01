package ru.art2000.calculator.view_model.currency

import androidx.lifecycle.LiveData
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.extensions.collections.LiveList

interface CurrenciesAddModel : CurrencyModificationModel {

    val hiddenItems: LiveData<List<CurrencyItem>>

    val selectedHiddenItems: LiveList<CurrencyItem>

    val displayedHiddenItems: LiveList<CurrencyItem>

    fun isHiddenItemSelected(item: CurrencyItem): Boolean

    fun setHiddenItemSelected(item: CurrencyItem, selected: Boolean)
}