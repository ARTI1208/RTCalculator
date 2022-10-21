package ru.art2000.calculator.model.currency

import okhttp3.OkHttpClient
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.art2000.calculator.view_model.currency.CbrAPI
import ru.art2000.extensions.platform.TLSSocketFactory
import ru.art2000.extensions.platform.platformTrustManager
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CbrBackend @Inject constructor(): CurrencyRemoteBackend {

    private val cbrService by lazy {
        val strategy = AnnotationStrategy()
        val serializer = Persister(strategy)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .sslSocketFactory(TLSSocketFactory(), platformTrustManager())
            .build()


        val retrofit = Retrofit.Builder()
            .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
            .baseUrl("https://www.cbr.ru")
            .client(okHttpClient)
            .build()

        retrofit.create(CbrAPI::class.java)
    }

    private val dateRegex = Regex("(?<day>\\d{2})[.](?<month>\\d{2})[.](?<year>\\d{4})")

    private var maxDate = Calendar.getInstance()

    override suspend fun getDaily() = cbrService.getDailyCurrencies().mapToDomain()

    override suspend fun getForDate(date: Calendar): CurrencyData {
        val day = date[Calendar.DAY_OF_MONTH]
        val month = date[Calendar.MONTH] + 1
        val year = date[Calendar.YEAR]

        val dayStr = if (day < 10) "0$day" else day
        val monthStr = if (month < 10) "0$month" else month
        return cbrService.getCurrenciesOnDate("$dayStr/$monthStr/$year").mapToDomain()
    }

    override suspend fun getFirstAvailableDate(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(1992, 7 - 1, 1) //1992, July 1st
        }
    }

    override suspend fun getLastAvailableDate(): Calendar {
        return maxDate
    }

    private fun CurrenciesList.mapToDomain(): CurrencyData {
        val match = dateRegex.matchEntire(date)
            ?: throw IllegalStateException("Wrong date format: $date")

        val day = match.groups["day"]!!.value.toInt()
        val month = match.groups["month"]!!.value.toInt()
        val year = match.groups["year"]!!.value.toInt()

        val calendar = Calendar.getInstance().apply { set(year, month - 1, day) }
        val date = calendar.timeInMillis

        if (date > maxDate.timeInMillis) {
            maxDate = calendar
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