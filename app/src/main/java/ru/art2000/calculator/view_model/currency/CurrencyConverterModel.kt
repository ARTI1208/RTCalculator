@file:Suppress("DEPRECATION")

package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyDownloadCallback
import ru.art2000.calculator.background.currency.CurrencyFunctions
import ru.art2000.calculator.model.currency.CurrencyRate
import ru.art2000.calculator.model.currency.LoadingState
import ru.art2000.calculator.model.currency.Valute
import ru.art2000.extensions.arch.context
import java.io.IOException


class CurrencyConverterModel(application: Application) : AndroidViewModel(application), CurrencyListAdapterModel {

    private val db = CurrencyDependencies.getCurrencyDatabase(application)

    private val mLoadingState = MutableLiveData(LoadingState.UNINITIALISED)

    val loadingState: LiveData<LoadingState> = mLoadingState

    val preferences = PreferenceManager.getDefaultSharedPreferences(application)!!

    private val mUpdateDate by lazy {
        MutableLiveData<String?>(preferences.getString(
                updateDateKey, context.getString(R.string.preloaded_currencies_date)))
    }

    val updateDate: LiveData<String?> by ::mUpdateDate

    val visibleList = db.currencyDao().getVisibleItems()

    var isFirstUpdateDone = false

    override var lastInputItemPosition: Int = -1

    override var lastInputItemValue: Double = 1.0

    val titleUpdatedString: String by lazy { context.getString(R.string.updated) }

    val preferenceListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == updateDateKey) {
                    mUpdateDate.postValue(sharedPreferences.getString(key, mUpdateDate.value))
                }
            }

    init {
        mLoadingState.observeForever {
            if (it != LoadingState.LOADING_STARTED && it != LoadingState.UNINITIALISED) {
                isFirstUpdateDone = true
                mLoadingState.postValue(LoadingState.UNINITIALISED)
            }
        }
        preferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key -> }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            CurrencyFunctions.downloadCurrencies(getApplication(), object : CurrencyDownloadCallback {
                override fun onDownloadStarted(): Boolean {
                    if (mLoadingState.value == LoadingState.LOADING_STARTED) return false

                    mLoadingState.postValue(LoadingState.LOADING_STARTED)
                    return true
                }

                override fun onDataOfSameDate(): Boolean {
                    mLoadingState.postValue(LoadingState.LOADING_ENDED)
                    return false
                }

                override fun onSuccess(date: String) {
                    mLoadingState.postValue(LoadingState.LOADING_ENDED)
                }

                override fun onException(exception: Exception) {
                    exception.printStackTrace()
                    val loadingState = when (exception) {
                        is IOException -> LoadingState.NETWORK_ERROR
                        else -> LoadingState.UNKNOWN_ERROR
                    }
                    mLoadingState.postValue(loadingState)
                }
            })
        }
    }

    fun isUpdateOnFirstTabOpenEnabled() =
            preferences.getBoolean(updateCurrenciesOnTabOpenKey, updateCurrenciesOnTabOpenDefault)

    companion object {

        const val updateDateKey = "currency_update_date"

        private const val updateCurrenciesOnTabOpenKey = "update_currencies_on_tab_open"

        private const val updateCurrenciesOnTabOpenDefault = true

    }
}