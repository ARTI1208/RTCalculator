package ru.art2000.calculator.currency_converter.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ru.art2000.calculator.currency_converter.model.CurrencyItem
import ru.art2000.calculator.currency_converter.model.LoadingState
import ru.art2000.helpers.CurrencyValuesHelper
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Pattern

class CurrencyConverterModel(application: Application) : AndroidViewModel(application) {

    private val db = CurrencyDependencies.getCurrencyDatabase(application)

    private val mLoadingState = MutableLiveData(LoadingState.UNINITIALISED)

    private val mUpdateDate = MutableLiveData<String?>()

    val loadingState: LiveData<LoadingState> = mLoadingState

    val updateDate: LiveData<String?> = db.currencyDao().getRefreshDate()

    val visibleList = db.currencyDao().getVisibleItems()


    init {
        mUpdateDate.observeForever {
            Single
                    .fromCallable { db.currencyDao().putRefreshDate(it) }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
        }
    }

    fun loadData() {
        mLoadingState.postValue(LoadingState.LOADING_STARTED)
        val webpage: Document?

        try {
            webpage = Jsoup.connect("http://www.cbr.ru/currency_base/daily/").get()
        } catch (e : Exception) {
            mLoadingState.postValue(LoadingState.NETWORK_ERROR)
            return
        }

        webpage ?: kotlin.run {
            mLoadingState.postValue(LoadingState.NETWORK_ERROR)
            return
        }


        try {
            val dateFormat = "[0-9]{2}.[0-9]{2}.[0-9]{4}"
            val datePattern = Pattern.compile(dateFormat)
            val dateBlock = webpage.select("h2").first().text()
            val matcher = datePattern.matcher(dateBlock)
            var date = ""

            if (matcher.find()) date = dateBlock.substring(matcher.start(), matcher.end())

            if (mUpdateDate.value == date) {
                mLoadingState.postValue(LoadingState.LOADING_ENDED)
                return
            }


            var table: Elements? = null
            for (el in webpage.select("table")) {
                if (el.hasClass("data")) {
                    table = el.children().first().children()
                }
            }

            if (table == null) {
                mLoadingState.postValue(LoadingState.UNKNOWN_ERROR)
                return
            }

            val ruVal = table.select("tr:contains(USD)").first().child(4).text().replace(',', '.').toDouble()
            val dot2dig: NumberFormat = DecimalFormat("#.##")
            table.removeAt(0)

            mUpdateDate.postValue(date)

            for (row in table) {
//                                Log.d("Цифр. код", row.child(0).text());
//                                Log.d("Букв. код", row.child(1).text());
//                                Log.d("Единиц", row.child(2).text());
//                                Log.d("Валюта", row.child(3).text());
//                                Log.d("Курс", row.child(4).text());
                val values = row.child(4).text().replace(',', '.').toDouble()
                val units = row.child(2).text().replace(',', '.').toDouble()
                val valuesPerUnit = dot2dig.format(ruVal / (values / units)).replace(',', '.').toDouble()
                val letterCode = row.child(1).text()

                if (db.currencyDao().updateRate(letterCode, valuesPerUnit) < 1) {
                    Log.d("Modeeel", "upd failed")
                    val currencyItem = CurrencyItem(letterCode, valuesPerUnit)
                    val ins = db.currencyDao().insert(currencyItem)
                    Log.d("Modeeel", "insert result $ins")
                }
            }

            if (db.currencyDao().updateRate("RUB", ruVal) < 1) {
                Log.d("Modeeel", "upd failed")
                val ins = db.currencyDao().insert(CurrencyItem("RUB", ruVal))
                Log.d("Modeeel", "insert result $ins")
            }

            mLoadingState.postValue(LoadingState.LOADING_ENDED)
        } catch (e: Exception) {
            e.printStackTrace()
            mLoadingState.postValue(LoadingState.UNKNOWN_ERROR)
        }
    }


}