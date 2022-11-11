package ru.art2000.calculator.currency.remote

import kotlinx.datetime.LocalDate
import ru.art2000.calculator.currency.model.CurrencyData

internal interface CurrencyRemoteBackend {

    suspend fun getDaily(): CurrencyData

    suspend fun getForDate(date: LocalDate): CurrencyData

    suspend fun getFirstAvailableDate(): LocalDate

    suspend fun getLastAvailableDate(): LocalDate

}