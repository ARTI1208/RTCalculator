package ru.art2000.calculator.calculator.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.art2000.calculator.calculator.model.HistoryItem
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