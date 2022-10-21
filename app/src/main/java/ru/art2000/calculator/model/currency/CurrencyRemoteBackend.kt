package ru.art2000.calculator.model.currency

import java.util.Calendar

interface CurrencyRemoteBackend {

    suspend fun getDaily(): CurrencyData

    suspend fun getForDate(date: Calendar): CurrencyData

    suspend fun getFirstAvailableDate(): Calendar

    suspend fun getLastAvailableDate(): Calendar

}