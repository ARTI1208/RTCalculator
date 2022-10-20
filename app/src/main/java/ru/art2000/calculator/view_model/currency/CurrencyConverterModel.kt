@file:Suppress("DEPRECATION")

package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyDownloadCallback
import ru.art2000.calculator.background.currency.CurrencyFunctions
import ru.art2000.calculator.model.currency.LoadingState
import ru.art2000.extensions.arch.context
import ru.art2000.helpers.CurrencyPreferenceHelper
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterModel @Inject constructor(
    @ApplicationContext application: Context,
    private val prefsHelper: CurrencyPreferenceHelper,
): AndroidViewModel(application as Application), CurrencyListAdapterModel {

    private val db = CurrencyDependencies.getCurrencyDatabase(application)

    private val mLoadingState = MutableLiveData(LoadingState.UNINITIALISED)

    val loadingState: LiveData<LoadingState> = mLoadingState

    private val mUpdateDate by lazy { MutableLiveData(prefsHelper.updateDate) }

    val updateDate: LiveData<String> by ::mUpdateDate

    val visibleList = db.currencyDao().getVisibleItems()

    var isFirstUpdateDone = false

    override var lastInputItemPosition: Int = -1

    override var lastInputItemValue: Double = savedInputItemValue

    override val savedInputItemCode: String
        get() = prefsHelper.conversionCode

    override val savedInputItemValue: Double
        get() = prefsHelper.conversionValue

    override fun saveConversionIfNeeded(code: String) {
        prefsHelper.putConversionValuesIfNeeded(code, lastInputItemValue)
    }

    val titleUpdatedString: String by lazy { context.getString(R.string.currency_date) }

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

    fun isUpdateOnFirstTabOpenEnabled() = prefsHelper.updateOnFirstTabOpen

    fun listenDateUpdate() {
        prefsHelper.updateDateProperty.listen { mUpdateDate.postValue(it) }
    }

    fun stopListeningDateUpdate() {
        prefsHelper.updateDateProperty.stopListening()
    }

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
}