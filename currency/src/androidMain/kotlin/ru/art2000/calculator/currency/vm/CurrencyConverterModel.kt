@file:Suppress("DEPRECATION")

package ru.art2000.calculator.currency.vm

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import ru.art2000.calculator.currency.background.CurrencyDownloadCallback
import ru.art2000.calculator.currency.model.LoadingState
import ru.art2000.calculator.currency.preferences.CurrencyPreferenceHelper
import ru.art2000.calculator.currency.repo.CurrencyRepository
import ru.art2000.extensions.preferences.Subscription
import ru.art2000.extensions.preferences.observe
import ru.art2000.extensions.timeInMillis
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
internal class CurrencyConverterModel @Inject constructor(
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

    private val currencyDownloadCallback = object : CurrencyDownloadCallback {
        override fun onDownloadStarted(): Boolean {
            if (mLoadingState.value == LoadingState.LOADING_STARTED) return true

            mLoadingState.value = LoadingState.LOADING_STARTED
            return false
        }

        override fun onDateFetched(date: LocalDate): Boolean {
            return prefsHelper.updateDateMillis == date.timeInMillis
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

    fun loadTimeIntervals(callback: (LocalDate, LocalDate) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val minDateMillis = repository.getFirstAvailableDate()
            val maxDateMillis = repository.getLastAvailableDate()

            viewModelScope.launch(Dispatchers.Default) {
                callback(minDateMillis, maxDateMillis)
            }
        }
    }

    fun loadData(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadAndStoreForDate(date, currencyDownloadCallback)
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.loadAndStoreDaily(currencyDownloadCallback)
        }
    }

    fun isUpdateOnFirstTabOpenEnabled() = prefsHelper.updateOnFirstTabOpen

    private var dateSubscription: Subscription? = null

    fun listenDateUpdate() {
        dateSubscription = prefsHelper.updateDateMillisProperty.observe {
            mUpdateDate.value = updateDateString(it)
        }
    }

    fun stopListeningDateUpdate() {
        dateSubscription?.invoke()
        dateSubscription = null
    }

    private fun updateDateString(timeMillis: Long): String {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return Instant
            .ofEpochMilli(timeMillis)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    }
}