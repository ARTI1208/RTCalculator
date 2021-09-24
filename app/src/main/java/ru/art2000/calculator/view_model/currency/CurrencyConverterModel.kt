@file:Suppress("DEPRECATION")

package ru.art2000.calculator.view_model.currency

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.art2000.calculator.R
import ru.art2000.calculator.model.currency.CurrencyRate
import ru.art2000.calculator.model.currency.LoadingState
import ru.art2000.calculator.model.currency.Valute
import ru.art2000.extensions.arch.context
import java.io.IOException
import java.util.concurrent.TimeUnit


class CurrencyConverterModel(application: Application) : AndroidViewModel(application), CurrencyListAdapterModel {

    private val db = CurrencyDependencies.getCurrencyDatabase(application)

    private val mLoadingState = MutableLiveData(LoadingState.UNINITIALISED)

    val loadingState: LiveData<LoadingState> = mLoadingState

    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    private val updateDateKey = "currency_update_date"

    private val mUpdateDate by lazy {
        MutableLiveData<String?>(preferences.getString(
                updateDateKey, context.getString(R.string.preloaded_currencies_date)))
    }

    val updateDate: LiveData<String?> = mUpdateDate

    val visibleList = db.currencyDao().getVisibleItems()

    var isFirstUpdateDone = false

    override var lastInputItemPosition: Int = -1

    override var lastInputItemValue: Double = 1.0

    val titleUpdatedString: String by lazy { context.getString(R.string.updated) }

    init {
        mLoadingState.observeForever {
            if (it != LoadingState.LOADING_STARTED && it != LoadingState.UNINITIALISED) {
                isFirstUpdateDone = true
                mLoadingState.postValue(LoadingState.UNINITIALISED)
            }
        }
    }

    fun loadData() {
        if (mLoadingState.value == LoadingState.LOADING_STARTED)
            return

        mLoadingState.postValue(LoadingState.LOADING_STARTED)

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

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currencies = cbrService.getDailyCurrencies()

                if (currencies.date == mUpdateDate.value) {
                    mLoadingState.postValue(LoadingState.LOADING_ENDED)
                    return@launch
                }

                mUpdateDate.postValue(currencies.date)
                preferences.edit { putString(updateDateKey, currencies.date) }

                val currencyRates = convertToCurrencyRates(currencies.valutes)
                db.currencyDao().update(currencyRates)

                mLoadingState.postValue(LoadingState.LOADING_ENDED)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                mLoadingState.postValue(LoadingState.NETWORK_ERROR)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                mLoadingState.postValue(LoadingState.UNKNOWN_ERROR)
            }
        }
    }

    private fun convertToCurrencyRates(valutes: List<Valute>): List<CurrencyRate> {
        val usdValute = valutes.first { it.charCode == "USD" }
        val usdValuteValue = usdValute.value

        return valutes.map {
            CurrencyRate(it.charCode, (usdValuteValue * it.quantity) / it.value)
        } + CurrencyRate("RUB", usdValuteValue)
    }

}