package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.view_model.currency.CurrencyDependencies.getNameIdentifierForCode
import ru.art2000.extensions.*
import ru.art2000.helpers.AndroidHelper
import ru.art2000.helpers.PrefsHelper
import java.util.*
import kotlin.collections.ArrayList

class CurrenciesSettingsModel(application: Application)
    : AndroidViewModel(application), CurrenciesAddModel, CurrenciesEditModel {

    private val mSelectedTab = MutableLiveData(0)

    var isFirstTimeTooltipShown = !PrefsHelper.isDeleteTooltipShown()

    var selectedTab: Int
        get() = mSelectedTab.value ?: -1
        set(value) = mSelectedTab.postValue(value)


    private val currencyDao = CurrencyDependencies.getCurrencyDatabase(application).currencyDao()

    fun makeItemsVisible(items: List<CurrencyItem>) {
        currencyDao.makeItemsVisible(items)
    }

    fun makeItemsHidden(items: List<CurrencyItem>) {
        currencyDao.makeItemsHidden(items)
    }

    fun completeSelectedHiddenItems() {
        selectedHiddenItems.addAllNew(displayedHiddenItems)
    }

    fun completeSelectedVisibleItems() {
        selectedVisibleItems.addAllNew(displayedVisibleItems)
    }

    private var lastSearchQuery = ""


    fun filterHiddenItems(query: String) {
        lastSearchQuery = query

        viewModelScope.launch(Job() + Dispatchers.Default) {

            val newList: List<CurrencyItem>

            if (query.isEmpty()) {
                newList = hiddenItems.value ?: listOf()
            } else {
                newList = ArrayList()
                val lowerQuery = query.toLowerCase()

                val mainLocale = Locale.getDefault()

                val allItems = hiddenItems.value ?: listOf()

                val context: Context = getApplication()

                for (item in allItems) {
                    val lowerCode = item.code.toLowerCase()
                    val itemNameResourceId = getNameIdentifierForCode(context, item.code)
                    val lowerName: String = context.getString(itemNameResourceId).toLowerCase()
                    if (lowerCode.contains(lowerQuery) || lowerName.contains(lowerQuery)
                            || (mainLocale != Locale.ENGLISH
                                    && AndroidHelper.getLocalizedString(context, Locale.ENGLISH, itemNameResourceId)
                                    .toLowerCase().contains(lowerQuery))) {
                        newList.add(item)
                    }
                }
            }

            viewModelScope.launch(Dispatchers.Main) {
                displayedHiddenItems.setAll(newList)
            }

//            val diff = calculateDiff(displayedHiddenItems, newList)
//            diff.dispatchUpdatesTo(object : ListUpdateCallback {
//                override fun onChanged(position: Int, count: Int, payload: Any?) {
//
//                }
//
//                override fun onMoved(fromPosition: Int, toPosition: Int) {
//
//                }
//
//                override fun onInserted(position: Int, count: Int) {
//                    displayedHiddenItems.addAll(position, newList.subList(position, position + count))
//                }
//
//                override fun onRemoved(position: Int, count: Int) {
//                    displayedHiddenItems.removeAll(position, count)
//                }
//
//            })

        }
    }

    override val hiddenItems = currencyDao.getHiddenItems()

    override val selectedHiddenItems: LiveList<CurrencyItem> = ArrayLiveList()

    override fun isHiddenItemSelected(item: CurrencyItem): Boolean {
        return selectedHiddenItems.contains(item)
    }

    override fun setHiddenItemSelected(item: CurrencyItem, selected: Boolean) {
        if (selected) {
            selectedHiddenItems.add(item)
        } else {
            selectedHiddenItems.remove(item)
        }
    }

    override val displayedHiddenItems: LiveList<CurrencyItem> = ArrayLiveList()

    override val visibleItems = currencyDao.getVisibleItems()

    override val selectedVisibleItems: LiveList<CurrencyItem> = ArrayLiveList()

    override fun isVisibleItemSelected(item: CurrencyItem): Boolean {
        return selectedVisibleItems.contains(item)
    }

    override fun setVisibleItemSelected(item: CurrencyItem, selected: Boolean) {
        if (selected) {
            selectedVisibleItems.add(item)
        } else {
            selectedVisibleItems.remove(item)
        }
    }

    override val displayedVisibleItems: LiveList<CurrencyItem> = ArrayLiveList()

    override var isEditSelectionMode: Boolean = false

    init {
        hiddenItems.observeForever {
            selectedHiddenItems.retainAll(it)
            filterHiddenItems(lastSearchQuery)
        }

        visibleItems.observeForever {
            selectedVisibleItems.retainAll(it)
            displayedVisibleItems.setAll(it)
        }


        displayedVisibleItems.observeForever(object : LiveList.LiveListObserver<CurrencyItem>() {
            override fun onAnyChanged(previousList: List<CurrencyItem>) {
                val copy = ArrayList(displayedVisibleItems)
                viewModelScope.launch(Job() + Dispatchers.IO) {
                    currencyDao.updateAll(copy)
                }
            }
        })
    }
}