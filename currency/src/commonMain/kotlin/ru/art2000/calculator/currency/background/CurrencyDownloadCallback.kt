package ru.art2000.calculator.currency.background

import kotlinx.datetime.LocalDate

internal interface CurrencyDownloadCallback {

    /**
     * @return whether processing should stop
     */
    fun onDownloadStarted(): Boolean = false

    /**
     * @return whether processing should stop
     */
    fun onDateFetched(date: LocalDate): Boolean = false

    fun onDownloadFinished() {}

    fun onException(exception: Exception) {}

}