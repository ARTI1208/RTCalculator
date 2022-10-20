package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.view_model.currency.CurrencyDependencies.getCurrencyDatabase
import ru.art2000.calculator.view_model.currency.CurrencyDependencies.getNameIdentifierForCode
import ru.art2000.extensions.collections.ArrayLiveList
import ru.art2000.extensions.collections.LiveList
import ru.art2000.extensions.writeAndUpdateUi
import ru.art2000.helpers.CurrencyPreferenceHelper
import ru.art2000.helpers.getLocalizedString
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class CurrenciesSettingsModel @Inject constructor(
    @ApplicationContext application: Context,
    prefsHelper: CurrencyPreferenceHelper,
) : AndroidViewModel(application as Application), CurrenciesAddModel, CurrenciesEditModel {

    private val mSelectedTab = MutableLiveData(0)

    val liveIsFirstTimeTooltipShown: MutableLiveData<Boolean> =
        MutableLiveData(!prefsHelper.isDeleteTooltipShown)

    var selectedTab: Int
        get() = mSelectedTab.value ?: -1
        set(value) {
            mSelectedTab.value = value
        }


    val removedItems: MutableLiveData<List<CurrencyItem>> = MutableLiveData(emptyList())

    private val currencyDao = getCurrencyDatabase(application).currencyDao()

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

                val mainLocale = Locale.getDefault()

                val lowerQuery = query.lowercase(mainLocale)

                val allItems = hiddenItems.value ?: listOf()

                val context: Context = getApplication()

                for (item in allItems) {
                    val lowerCode = item.code.lowercase(mainLocale)
                    val itemNameResourceId = getNameIdentifierForCode(context, item.code)
                    val lowerName: String = context.getString(itemNameResourceId)
                        .lowercase(mainLocale)
                    if (lowerCode.contains(lowerQuery) || lowerName.contains(lowerQuery)
                        || (mainLocale != Locale.ENGLISH
                                && context.getLocalizedString(
                            Locale.ENGLISH,
                            itemNameResourceId
                        )
                            .lowercase(Locale.ENGLISH).contains(lowerQuery))
                    ) {
                        newList.add(item)
                    }
                }
            }

            viewModelScope.launch(Dispatchers.Main) {
                displayedHiddenItems.setAll(newList)
            }
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

    override fun databaseMarkHidden(item: CurrencyItem) {
        writeAndUpdateUi(
            compute = { currencyDao.removeFromVisible(item.code) },
            update = { removedItems.value = listOf(item) }
        )
    }

    override fun dismissFirstTimeTooltip() {
        liveIsFirstTimeTooltipShown.value = false
    }

    override val displayedVisibleItems: LiveList<CurrencyItem> = ArrayLiveList()

    override var isEditSelectionMode: Boolean = false


    val liveQuery: MutableLiveData<String> = MutableLiveData("")

    override val currentQuery: String
        get() = liveQuery.value!!

    override val recyclerViewBottomPadding: MutableLiveData<Int> = MutableLiveData(0)

    fun observeAndUpdateDisplayedItems(lifecycleOwner: LifecycleOwner) {
        hiddenItems.observe(lifecycleOwner) {
            selectedHiddenItems.retainAll(it)
            filterHiddenItems(lastSearchQuery)
        }

        visibleItems.observe(lifecycleOwner) {
            selectedVisibleItems.retainAll(it)
            displayedVisibleItems.setAll(it)
        }

        displayedVisibleItems.observe(lifecycleOwner, object : LiveList.LiveListObserver<CurrencyItem>() {
            override fun onAnyChanged(
                previousList: List<CurrencyItem>,
                liveList: LiveList<CurrencyItem>
            ) {
                val copy = liveList.snapshot()
                viewModelScope.launch(Job() + Dispatchers.IO) {
                    currencyDao.updateAll(copy)
                }
            }
        })
    }
}