@file:Suppress("DEPRECATION")

package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyDownloadCallback
import ru.art2000.calculator.model.currency.CurrencyRepository
import ru.art2000.calculator.model.currency.LoadingState
import ru.art2000.extensions.arch.context
import ru.art2000.helpers.CurrencyPreferenceHelper
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterModel @Inject constructor(
    @ApplicationContext application: Context,
    private val prefsHelper: CurrencyPreferenceHelper,
    private val repository: CurrencyRepository,
): AndroidViewModel(application as Application), CurrencyListAdapterModel {

    private val mLoadingState = MutableStateFlow(LoadingState.UNINITIALISED)

    val loadingState: StateFlow<LoadingState> = mLoadingState

    private val mUpdateDate by lazy { MutableStateFlow(updateDateString(prefsHelper.updateDateMillis)) }

    val updateDate: StateFlow<String> by ::mUpdateDate

    val visibleList = repository.getVisibleItems()

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
            if (mLoadingState.value == LoadingState.LOADING_STARTED) return true

            mLoadingState.value = LoadingState.LOADING_STARTED
            return false
        }

        override fun onDateFetched(date: Long): Boolean {
            return prefsHelper.updateDateMillis == date
        }

        override fun onDownloadFinished() {
            mLoadingState.value = LoadingState.LOADING_ENDED
        }

        override fun onException(exception: Exception) {
            exception.printStackTrace()
            val loadingState = when (exception) {
                is IOException -> LoadingState.NETWORK_ERROR
                else -> LoadingState.UNKNOWN_ERROR
            }
            mLoadingState.value = loadingState
        }
    }

    init {
        viewModelScope.launch {
            mLoadingState.collect {
                if (!it.finishesLoading) return@collect

                isFirstUpdateDone = true
                mLoadingState.value = LoadingState.UNINITIALISED
            }
        }
    }

    fun loadTimeIntervals(callback: (Long, Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val minDateMillis = repository.getFirstAvailableDate().timeInMillis
            val maxDateMillis = repository.getLastAvailableDate().timeInMillis

            viewModelScope.launch(Dispatchers.Default) {
                callback(minDateMillis, maxDateMillis)
            }
        }
    }

    fun loadData(dateMillis: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadAndStoreForDate(dateMillis, currencyDownloadCallback)
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadAndStoreDaily(currencyDownloadCallback)
        }
    }

    fun isUpdateOnFirstTabOpenEnabled() = prefsHelper.updateOnFirstTabOpen

    fun listenDateUpdate() {
        prefsHelper.updateDateMillisProperty.listen { mUpdateDate.value = updateDateString(it) }
    }

    fun stopListeningDateUpdate() {
        prefsHelper.updateDateMillisProperty.stopListening()
    }

    private fun updateDateString(timeMillis: Long): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return Instant
            .ofEpochMilli(timeMillis)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }
}