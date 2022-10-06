package ru.art2000.calculator.background.currency

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.work.*
import okhttp3.OkHttpClient
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.art2000.calculator.R
import ru.art2000.calculator.model.currency.CurrenciesList
import ru.art2000.calculator.model.currency.CurrencyRate
import ru.art2000.calculator.model.currency.Valute
import ru.art2000.calculator.view_model.currency.CbrAPI
import ru.art2000.calculator.view_model.currency.CurrencyConverterModel
import ru.art2000.calculator.view_model.currency.CurrencyDependencies
import java.util.concurrent.TimeUnit

object CurrencyFunctions {

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
            "wifi" -> NetworkType.UNMETERED
            "any" -> NetworkType.CONNECTED
            "no_update" -> {
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
        Log.d("CalcEnq", "enqueued for $downloadType")
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName,
            existingWorkPolicy,
            downloadCurrenciesRequest
        )
    }

    suspend fun downloadCurrencies(
        context: Context,
        callback: CurrencyDownloadCallback,
    ) = downloadCurrencies(
            context,
            CbrAPI::getDailyCurrencies,
            callback
        )

    suspend fun downloadCurrencies(
        context: Context,
        year: Int,
        month: Int,
        day: Int,
        callback: CurrencyDownloadCallback,
    ) = downloadCurrencies(
        context,
        {
            val dayStr = if (day < 10) "0$day" else day
            val monthStr = if (month < 10) "0$month" else month
            getCurrenciesOnDate("$dayStr/$monthStr/$year")
        },
        callback
    )

    private suspend fun downloadCurrencies(
        context: Context,
        currenciesGetter: suspend CbrAPI.() -> CurrenciesList,
        callback: CurrencyDownloadCallback,
    ) {

        callback.onDownloadStarted()

        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build()


        val retrofit = Retrofit.Builder()
            .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
            .baseUrl("http://www.cbr.ru")
            .client(okHttpClient)
            .build()

        val cbrService = retrofit.create(CbrAPI::class.java)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        try {
            val currencies = cbrService.currenciesGetter()

            if (currencies.date == preferences.getCurrentUpdateDate(context)) {
                callback.onDataOfSameDate()
                return
            }
            callback.onSuccess(currencies.date)

            preferences.edit { putString(CurrencyConverterModel.updateDateKey, currencies.date) }

            val currencyRates = convertToCurrencyRates(currencies.valutes)
            CurrencyDependencies.getCurrencyDatabase(context).currencyDao().update(currencyRates)
        } catch (e: Exception) {
            callback.onException(e)
        }
    }

    private fun SharedPreferences.getCurrentUpdateDate(context: Context): String = getString(
        CurrencyConverterModel.updateDateKey,
        context.getString(R.string.preloaded_currencies_date)
    )!!

    private fun convertToCurrencyRates(valutes: List<Valute>): List<CurrencyRate> {
        val usdValute = valutes.first { it.charCode == "USD" }
        val usdValuteValue = usdValute.value

        return valutes.map {
            CurrencyRate(it.charCode, (usdValuteValue * it.quantity) / it.value)
        } + CurrencyRate("RUB", usdValuteValue)
    }
}