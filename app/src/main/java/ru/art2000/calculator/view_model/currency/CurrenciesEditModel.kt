package ru.art2000.calculator.view_model.currency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.view_model.currency.CurrencyDependencies

class CurrenciesEditModel(application: Application) : AndroidViewModel(application) {

    private val currencyDao = CurrencyDependencies.getCurrencyDatabase(application).currencyDao()

    val visibleItems = currencyDao.getVisibleItems()

}