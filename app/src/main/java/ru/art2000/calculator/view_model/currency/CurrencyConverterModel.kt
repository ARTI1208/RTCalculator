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
import ru.art2000.calculator.model.currency.LoadingState
import ru.art2000.extensions.arch.context
import java.io.IOException
import java.util.*


class CurrencyConverterModel(application: Application) : AndroidViewModel(application), CurrencyListAdapterModel {

    private val db = CurrencyDependencies.getCurrencyDatabase(application)

    private val mLoadingState = MutableLiveData(LoadingState.UNINITIALISED)

    val loadingState: LiveData<LoadingState> = mLoadingState

    val preferences = PreferenceManager.getDefaultSharedPreferences(application)!!

    private val mUpdateDate by lazy {
        MutableLiveData<String>(preferences.getString(
                updateDateKey, context.getString(R.string.preloaded_currencies_date)))
    }

    val updateDate: LiveData<String> by ::mUpdateDate

    val visibleList = db.currencyDao().getVisibleItems()

    var isFirstUpdateDone = false

    override var lastInputItemPosition: Int = -1

    override var lastInputItemValue: Double = 1.0

    val titleUpdatedString: String by lazy { context.getString(R.string.currency_date) }

    val preferenceListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == updateDateKey) {
                    mUpdateDate.postValue(sharedPreferences.getString(key, mUpdateDate.value))
                }
            }

    private val currencyDownloadCallback = object : CurrencyDownloadCallback {
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
            maxDateMillis = millisFromString(date).coerceAtLeast(maxDateMillis)
        }

        override fun onException(exception: Exception) {
            exception.printStackTrace()
            val loadingState = when (exception) {
                is IOException -> LoadingState.NETWORK_ERROR
                else -> LoadingState.UNKNOWN_ERROR
            }
            mLoadingState.postValue(loadingState)
        }
    }

    init {
        mLoadingState.observeForever {
            if (it != LoadingState.LOADING_STARTED && it != LoadingState.UNINITIALISED) {
                isFirstUpdateDone = true
                mLoadingState.postValue(LoadingState.UNINITIALISED)
            }
        }
    }

    private var checked = false

    // July 1st, 1992
    val minDateMillis = millisFromDate(1992, 7, 1)
    var maxDateMillis = millisFromDate()
        get() {
            if (!checked) {
                checked = true
                field = field.coerceAtLeast(millisFromString(mUpdateDate.value!!))
            }

            return field
        }
        private set

    fun loadData(year: Int, month: Int, day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            CurrencyFunctions.downloadCurrencies(
                getApplication(),
                year, month, day,
                currencyDownloadCallback)
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            CurrencyFunctions.downloadCurrencies(getApplication(), currencyDownloadCallback)
        }
    }

    fun isUpdateOnFirstTabOpenEnabled() =
            preferences.getBoolean(updateCurrenciesOnTabOpenKey, updateCurrenciesOnTabOpenDefault)

    private fun millisFromString(date: String): Long {
        val day = date.takeWhile { it != '.' }.toInt()
        val month = date.drop(3).takeWhile { it != '.' }.toInt()
        val year = date.takeLastWhile { it != '.' }.toInt()

        check(day in 1..31)
        check(month in 1..12)
        check(year > 1990)

        return millisFromDate(year, month, day)
    }

    private fun millisFromDate(year: Int, month: Int, day: Int): Long {
        val c = Calendar.getInstance()
        c.set(year, month - 1, day)

        return c.timeInMillis
    }

    private fun millisFromDate(): Long {
        val c = Calendar.getInstance()
        return c.timeInMillis
    }

    companion object {

        const val updateDateKey = "currency_update_date"

        private const val updateCurrenciesOnTabOpenKey = "update_currencies_on_tab_open"

        private const val updateCurrenciesOnTabOpenDefault = true

    }
}