package ru.art2000.calculator.view_model.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.model.calculator.HistoryItem
import ru.art2000.calculator.view_model.calculator.CalculatorDependencies
import kotlin.concurrent.thread

class CalculatorModel(application: Application) : AndroidViewModel(application) {

    private val historyDao = CalculatorDependencies.getHistoryDatabase(application).historyDao()

    val historyItems = historyDao.getAll()

    fun removeHistoryItem(id: Int) {
        thread {
            historyDao.deleteById(id)
        }
    }

    fun clearDatabase() {
        thread {
            historyDao.clear()
        }
    }

    fun saveCalculationResult(expression: String, result: String) {
        thread {
            historyDao.insert(HistoryItem(expression, result))
        }
    }
}