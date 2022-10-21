package ru.art2000.calculator.model.currency

import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.background.currency.CurrencyDownloadCallback
import ru.art2000.helpers.CurrencyPreferenceHelper
import java.util.*
import javax.inject.Inject

class DefaultCurrencyRepo @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val remoteBackend: CurrencyRemoteBackend,
    private val prefsHelper: CurrencyPreferenceHelper,
) : CurrencyRepository {

    override fun getVisibleItems(): Flow<List<CurrencyItem>> {
        return currencyDao.getVisibleItems()
    }

    override fun getHiddenItems(): Flow<List<CurrencyItem>> {
        return currencyDao.getHiddenItems()
    }

    override suspend fun updateRates(items: List<CurrencyRate>): Int {
        return currencyDao.updateRates(items)
    }

    override suspend fun removeFromVisible(code: String) {
        return currencyDao.removeFromVisible(code)
    }

    override suspend fun makeItemsVisible(items: List<CurrencyItem>) {
        return currencyDao.makeItemsVisible(items)
    }

    override suspend fun makeItemsHidden(items: List<CurrencyItem>) {
        return currencyDao.makeItemsHidden(items)
    }

    override suspend fun updateAll(items: List<CurrencyItem>): Int {
        return currencyDao.updateAll(items)
    }

    private suspend fun loadAndStore(callback: CurrencyDownloadCallback, loader: suspend () -> CurrencyData) {
        if (callback.onDownloadStarted()) return
        try {
            val currencies = loader()
            if (callback.onDateFetched(currencies.date)) {
                callback.onDownloadFinished()
                return
            }
            callback.onDownloadFinished()
            prefsHelper.updateDateMillis = currencies.date
            updateRates(currencies.items)
        } catch (e: Exception) {
            callback.onException(e)
        }
    }

    override suspend fun loadAndStoreDaily(callback: CurrencyDownloadCallback) {
        loadAndStore(callback, remoteBackend::getDaily)
    }

    override suspend fun loadAndStoreForDate(dateMillis: Long, callback: CurrencyDownloadCallback) {
        loadAndStore(callback) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateMillis
            remoteBackend.getForDate(calendar)
        }
    }

    override suspend fun getFirstAvailableDate(): Calendar {
        return remoteBackend.getFirstAvailableDate()
    }

    override suspend fun getLastAvailableDate(): Calendar {
        return remoteBackend.getLastAvailableDate()
    }
}