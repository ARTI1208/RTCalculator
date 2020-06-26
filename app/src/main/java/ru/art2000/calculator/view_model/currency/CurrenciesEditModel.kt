package ru.art2000.calculator.view_model.currency

import androidx.databinding.ObservableList
import androidx.lifecycle.LiveData
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.extensions.LiveList
import ru.art2000.extensions.MutableLiveMap

interface CurrenciesEditModel {

    val visibleItems: LiveData<List<CurrencyItem>>

    val selectedVisibleItems: LiveList<CurrencyItem>

    fun isVisibleItemSelected(item: CurrencyItem): Boolean

    fun setVisibleItemSelected(item: CurrencyItem, selected: Boolean)

    val displayedVisibleItems: LiveList<CurrencyItem>

    var isEditSelectionMode: Boolean
}