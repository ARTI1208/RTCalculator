@file:Suppress("DEPRECATION")

package ru.art2000.calculator.currency.remote.cbr

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import okhttp3.OkHttpClient
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.art2000.calculator.currency.remote.CurrencyRemoteBackend
import ru.art2000.calculator.currency.model.CurrencyData
import ru.art2000.calculator.currency.model.CurrencyRate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class CbrBackend @Inject constructor(): CurrencyRemoteBackend {

    private val cbrService by lazy {
        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build()


        val retrofit = Retrofit.Builder()
            .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
            .baseUrl("https://www.cbr.ru")
            .client(okHttpClient)
            .build()

        retrofit.create(CbrAPI::class.java)
    }

    // Named groups are available on API23+ only
    private val dateRegex = Regex("(\\d{2})[.](\\d{2})[.](\\d{4})")

    private var maxDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    override suspend fun getDaily() = cbrService.getDailyCurrencies().mapToDomain()

    override suspend fun getForDate(date: LocalDate): CurrencyData {
        val day = date.dayOfMonth
        val month = date.monthNumber
        val year = date.year

        val dayStr = if (day < 10) "0$day" else day
        val monthStr = if (month < 10) "0$month" else month
        return cbrService.getCurrenciesOnDate("$dayStr/$monthStr/$year").mapToDomain()
    }

    override suspend fun getFirstAvailableDate(): LocalDate {
        return LocalDate(1992, 7, 1) //1992, July 1st
    }

    override suspend fun getLastAvailableDate(): LocalDate {
        return maxDate
    }

    private fun CurrenciesList.mapToDomain(): CurrencyData {
        val match = dateRegex.matchEntire(date)
            ?: throw IllegalStateException("Wrong date format: $date")

        val day = match.groups[1]!!.value.toInt()
        val month = match.groups[2]!!.value.toInt()
        val year = match.groups[3]!!.value.toInt()

        val date = LocalDate(year, month, day)

        if (date > maxDate) {
            maxDate = date
        }

        val usdValute = valutes.first { it.charCode == "USD" }
        val usdValuteValue = usdValute.value

        val items = valutes.map {
            CurrencyRate(
                it.charCode, (usdValuteValue * it.quantity) / it.value
            )
        } + CurrencyRate("RUB", usdValuteValue)

        return CurrencyData(date, items)
    }
}