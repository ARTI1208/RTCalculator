package ru.art2000.calculator.currency_converter.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.currency_converter.model.CurrencyItem

class CurrenciesSettingsModel(application: Application) : AndroidViewModel(application) {

    private val currencyDao = CurrencyDependencies.getCurrencyDatabase(application).currencyDao()

    fun makeItemsVisible(items: List<CurrencyItem>) {
        currencyDao.makeItemsVisible(items)
    }

    fun makeItemsHidden(items: List<CurrencyItem>) {
        Log.d("hidThe", "nuu")
        currencyDao.makeItemsHidden(items)
    }
}