package ru.art2000.calculator.background.currency

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class CurrencyDownloadWorker(context: Context, workerParameters: WorkerParameters): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        var result = Result.success()

        CurrencyFunctions.downloadCurrencies(applicationContext, object : CurrencyDownloadCallback {

            override fun onException(exception: Exception) {
                exception.printStackTrace()
                result = Result.failure()
            }
        })

        return result
    }
}