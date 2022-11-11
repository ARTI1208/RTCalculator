package ru.art2000.calculator.currency.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import ru.art2000.calculator.currency.background.CurrencyDownloadCallback
import ru.art2000.calculator.currency.db.CurrencyDao
import ru.art2000.calculator.currency.db.model.CurrencyDbItem
import ru.art2000.calculator.currency.db.model.toDbModel
import ru.art2000.calculator.currency.db.model.toDomainModel
import ru.art2000.calculator.currency.model.CurrencyData
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.calculator.currency.model.CurrencyRate
import ru.art2000.calculator.currency.remote.CurrencyRemoteBackend
import ru.art2000.calculator.currency.preferences.CurrencyPreferenceHelper
import ru.art2000.extensions.timeInMillis
import javax.inject.Inject

internal class DefaultCurrencyRepo @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val remoteBackend: CurrencyRemoteBackend,
    private val prefsHelper: CurrencyPreferenceHelper,
) : CurrencyRepository {

    private fun List<CurrencyItem>.toDbItemModels() = map { it.toDbModel() }

    private fun List<CurrencyRate>.toDbRateModels() = map { it.toDbModel() }

    private fun Flow<List<CurrencyDbItem>>.toDomainModelFlow() =
        map { items -> items.map { it.toDomainModel() } }

    override fun getVisibleItems(): Flow<List<CurrencyItem>> {
        return currencyDao.getVisibleItems().toDomainModelFlow()
    }

    override fun getHiddenItems(): Flow<List<CurrencyItem>> {
        return currencyDao.getHiddenItems().toDomainModelFlow()
    }

    override suspend fun updateRates(items: List<CurrencyRate>): Int {
        return currencyDao.updateRates(items.toDbRateModels())
    }

    override suspend fun removeFromVisible(code: String) {
        return currencyDao.removeFromVisible(code)
    }

    override suspend fun makeItemsVisible(items: List<CurrencyItem>) {
        return currencyDao.makeItemsVisible(items.toDbItemModels())
    }

    override suspend fun makeItemsHidden(items: List<CurrencyItem>) {
        return currencyDao.makeItemsHidden(items.toDbItemModels())
    }

    override suspend fun updateAll(items: List<CurrencyItem>): Int {
        return currencyDao.updateAll(items.toDbItemModels())
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
            prefsHelper.updateDateMillis = currencies.date.timeInMillis
            updateRates(currencies.items)
        } catch (e: Exception) {
            callback.onException(e)
        }
    }

    override suspend fun loadAndStoreDaily(callback: CurrencyDownloadCallback) {
        loadAndStore(callback, remoteBackend::getDaily)
    }

    override suspend fun loadAndStoreForDate(date: LocalDate, callback: CurrencyDownloadCallback) {
        loadAndStore(callback) { remoteBackend.getForDate(date) }
    }

    override suspend fun getFirstAvailableDate(): LocalDate {
        return remoteBackend.getFirstAvailableDate()
    }

    override suspend fun getLastAvailableDate(): LocalDate {
        return remoteBackend.getLastAvailableDate()
    }
}