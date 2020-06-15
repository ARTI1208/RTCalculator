package ru.art2000.calculator.view_model.currency

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.view_model.currency.CurrencyDependencies

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