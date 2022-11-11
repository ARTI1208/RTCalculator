package ru.art2000.calculator.currency.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import ru.art2000.calculator.currency.background.CurrencyDownloadCallback
import ru.art2000.calculator.currency.model.CurrencyItem
import ru.art2000.calculator.currency.model.CurrencyRate

internal interface CurrencyRepository {

    fun getVisibleItems(): Flow<List<CurrencyItem>>

    fun getHiddenItems(): Flow<List<CurrencyItem>>

    suspend fun updateRates(items: List<CurrencyRate>): Int

    suspend fun removeFromVisible(code: String)

    suspend fun makeItemsVisible(items: List<CurrencyItem>)

    suspend fun makeItemsHidden(items: List<CurrencyItem>)

    suspend fun updateAll(items: List<CurrencyItem>): Int


    suspend fun loadAndStoreDaily(callback: CurrencyDownloadCallback)

    suspend fun loadAndStoreForDate(date: LocalDate, callback: CurrencyDownloadCallback)

    suspend fun getFirstAvailableDate(): LocalDate

    suspend fun getLastAvailableDate(): LocalDate

}