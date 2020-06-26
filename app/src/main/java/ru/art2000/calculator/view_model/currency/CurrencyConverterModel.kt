package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.content.Context
import androidx.core.content.SharedPreferencesCompat
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.model.currency.LoadingState
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.regex.Pattern

class CurrencyConverterModel(application: Application) : AndroidViewModel(application), CurrencyListAdapterModel {

    private val db = CurrencyDependencies.getCurrencyDatabase(application)

    private val mLoadingState = MutableLiveData(LoadingState.UNINITIALISED)

    val loadingState: LiveData<LoadingState> = mLoadingState

    private val preferences = PreferenceManager.getDefaultSharedPreferences(application)

    private val updateDateKey = "currency_update_date"

    private val mUpdateDate = MutableLiveData<String?>(preferences.getString(updateDateKey, "Unknown"))

    val updateDate: LiveData<String?> = mUpdateDate

    val visibleList = db.currencyDao().getVisibleItems()

    var isFirstUpdateDone = false

    override var lastInputItemPosition: Int = -1

    override var lastInputItemValue: Double = 1.0

    init {
        mLoadingState.observeForever {
            if (it != LoadingState.LOADING_STARTED && it != LoadingState.UNINITIALISED) {
                mLoadingState.postValue(LoadingState.UNINITIALISED)
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun loadData() {

        if (mLoadingState.value == LoadingState.LOADING_STARTED)
            return

        viewModelScope.launch(Job() + Dispatchers.Default) {
            isFirstUpdateDone = true
            mLoadingState.postValue(LoadingState.LOADING_STARTED)
            val webpage: Document?

            try {
                webpage = Jsoup.connect("http://www.cbr.ru/currency_base/daily/").get()
            } catch (e: Exception) {
                e.printStackTrace()
                mLoadingState.postValue(LoadingState.NETWORK_ERROR)
                return@launch
            }

            webpage ?: kotlin.run {
                mLoadingState.postValue(LoadingState.NETWORK_ERROR)
                return@launch
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
                    return@launch
                }


                var table: Elements? = null
                for (el in webpage.select("table")) {
                    if (el.hasClass("data")) {
                        table = el.children().first().children()
                    }
                }

                if (table == null) {
                    mLoadingState.postValue(LoadingState.UNKNOWN_ERROR)
                    return@launch
                }

                val ruVal = table.select("tr:contains(USD)").first().child(4).text().replace(',', '.').toDouble()
                val dot2dig: NumberFormat = DecimalFormat("#.##")
                table.removeAt(0)

                mUpdateDate.postValue(date)
                preferences.edit {
                    putString(updateDateKey, date)
                }


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
                        val currencyItem = CurrencyItem(letterCode, valuesPerUnit)
                        val ins = db.currencyDao().insert(currencyItem)
                    }
                }

                if (db.currencyDao().updateRate("RUB", ruVal) < 1) {
                    db.currencyDao().insert(CurrencyItem("RUB", ruVal))
                }

                mLoadingState.postValue(LoadingState.LOADING_ENDED)
            } catch (e: Exception) {
                e.printStackTrace()
                mLoadingState.postValue(LoadingState.UNKNOWN_ERROR)
            }
        }
    }
}