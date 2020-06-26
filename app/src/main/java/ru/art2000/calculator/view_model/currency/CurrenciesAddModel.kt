package ru.art2000.calculator.view_model.currency

import androidx.lifecycle.LiveData
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.extensions.LiveList
import ru.art2000.extensions.MutableLiveMap

interface CurrenciesAddModel {

    val hiddenItems: LiveData<List<CurrencyItem>>

    val selectedHiddenItems: LiveList<CurrencyItem>

    fun isHiddenItemSelected(item: CurrencyItem): Boolean

    fun setHiddenItemSelected(item: CurrencyItem, selected: Boolean)

    val displayedHiddenItems: LiveList<CurrencyItem>
}