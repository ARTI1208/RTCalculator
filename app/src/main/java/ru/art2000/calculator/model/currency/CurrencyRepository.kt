package ru.art2000.calculator.model.currency

import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.background.currency.CurrencyDownloadCallback
import java.util.Calendar

interface CurrencyRepository {

    fun getVisibleItems(): Flow<List<CurrencyItem>>

    fun getHiddenItems(): Flow<List<CurrencyItem>>

    suspend fun updateRates(items: List<CurrencyRate>): Int

    suspend fun removeFromVisible(code: String)

    suspend fun makeItemsVisible(items: List<CurrencyItem>)

    suspend fun makeItemsHidden(items: List<CurrencyItem>)

    suspend fun updateAll(items: List<CurrencyItem>): Int


    suspend fun loadAndStoreDaily(callback: CurrencyDownloadCallback)

    suspend fun loadAndStoreForDate(dateMillis: Long, callback: CurrencyDownloadCallback)

    suspend fun getFirstAvailableDate(): Calendar

    suspend fun getLastAvailableDate(): Calendar

}