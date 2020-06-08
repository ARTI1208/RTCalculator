package ru.art2000.calculator.currency_converter.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class CurrenciesAddModel(application: Application) : AndroidViewModel(application) {


    private val currencyDao = CurrencyDependencies.getCurrencyDatabase(application).currencyDao()


    val hiddenItems = currencyDao.getHiddenItems()

}