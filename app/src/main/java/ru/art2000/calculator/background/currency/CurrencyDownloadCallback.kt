package ru.art2000.calculator.background.currency

import java.lang.Exception

interface CurrencyDownloadCallback {

    /**
     * @return whether download should continue
     */
    fun onDownloadStarted(): Boolean = true

    /**
     * @return whether download should continue
     */
    fun onDataOfSameDate(): Boolean = false

    fun onSuccess(date: String) {}

    fun onException(exception: Exception) {}

}