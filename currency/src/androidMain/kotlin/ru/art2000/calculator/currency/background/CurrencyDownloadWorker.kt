package ru.art2000.calculator.currency.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.art2000.calculator.currency.preferences.CurrencyValues
import ru.art2000.calculator.currency.repo.CurrencyRepository
import java.util.concurrent.TimeUnit

@HiltWorker
internal class CurrencyDownloadWorker @AssistedInject constructor (
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: CurrencyRepository,
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        var result = Result.success()

        repository.loadAndStoreDaily(object : CurrencyDownloadCallback {

            override fun onException(exception: Exception) {
                exception.printStackTrace()
                result = Result.failure()
            }
        })

        return result
    }

    companion object {
        fun setupCurrencyDownload(
            context: Context,
            downloadType: String,
            hours: Int,
            existingWorkPolicy: ExistingPeriodicWorkPolicy,
        ) {
            require(hours in 1..24)

            val workManager = WorkManager.getInstance(context)
            val uniqueWorkName = "downloadCurrencies"

            val networkType = when (downloadType) {
                CurrencyValues.VALUE_CURRENCY_BACKGROUND_WIFI -> NetworkType.UNMETERED
                CurrencyValues.VALUE_CURRENCY_BACKGROUND_ANY -> NetworkType.CONNECTED
                CurrencyValues.VALUE_CURRENCY_BACKGROUND_NO_UPDATE -> {
                    workManager.cancelUniqueWork(uniqueWorkName)
                    return
                }
                else -> throw IllegalArgumentException("Unknown download type '$downloadType'")
            }

            val downloadCurrenciesRequest =
                PeriodicWorkRequestBuilder<CurrencyDownloadWorker>(hours.toLong(), TimeUnit.HOURS)
                    .setInitialDelay(hours.toLong(), TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(networkType)
                            .setRequiresBatteryNotLow(true)
                            .build()
                    ).build()

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName,
                existingWorkPolicy,
                downloadCurrenciesRequest
            )
        }
    }
}