package ru.art2000.calculator.background.currency

import java.lang.Exception

interface CurrencyDownloadCallback {

    /**
     * @return whether processing should stop
     */
    fun onDownloadStarted(): Boolean = false

    /**
     * @return whether processing should stop
     */
    fun onDateFetched(date: Long): Boolean = false

    fun onDownloadFinished() {}

    fun onException(exception: Exception) {}

}