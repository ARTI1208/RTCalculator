package ru.art2000.calculator.view_model.calculator

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.art2000.calculator.R
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.HistoryItem
import ru.art2000.extensions.context
import kotlin.concurrent.thread

class CalculatorModel(application: Application) : AndroidViewModel(application), HistoryViewModel {

    private val historyDao = CalculatorDependencies.getHistoryDatabase(application).historyDao()

    val currentExpression: MutableLiveData<String> = MutableLiveData("0")
    val currentResult: MutableLiveData<String?> = MutableLiveData(null)
    val currentMemory: MutableLiveData<Double> = MutableLiveData(0.0)
    val currentAngleType: MutableLiveData<AngleType> = MutableLiveData(AngleType.DEGREES)

    override fun getHistoryItems(): LiveData<List<HistoryItem>> {
        return historyDao.getAll()
    }

    override fun copyHistoryItemToClipboard(item: HistoryItem, type: Int): String {
        val clip: ClipData?
        val copiedText: String?

        when (type) {
            HistoryViewModel.COPY_EXPR -> {
                copiedText = item.expression
                clip = ClipData.newPlainText("Expression", copiedText)
            }
            HistoryViewModel.COPY_RES -> {
                copiedText = item.result
                clip = ClipData.newPlainText("Result", copiedText)
            }
            HistoryViewModel.COPY_ALL -> {
                copiedText = item.fullExpression
                clip = ClipData.newPlainText("AllInOne", copiedText)
            }
            else -> return context.getString(R.string.error)
        }

        val cmg = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                ?: return "Error getting access to clipboard"

        cmg.setPrimaryClip(clip)

        return context.getString(R.string.copied) + " " + copiedText
    }

    override fun removeHistoryItem(id: Int) {
        thread {
            historyDao.deleteById(id)
        }
    }

    override fun clearHistoryDatabase() {
        thread {
            historyDao.clear()
        }
    }

    fun saveCalculationResult(expression: String, result: String) {
        thread {
            historyDao.insert(HistoryItem(expression, result))
        }
    }

    fun getNewExpressionForPreUnarySign(currentExpression: String, sign: String): String {
        if (currentExpression.isEmpty() || currentExpression == "0") return sign

        val last: String = currentExpression.substring(currentExpression.lastIndex)
        var append = ""
        if (CalculationClass.isDot(last))
            append = "0×"
        else if (CalculationClass.isNumber(last))
            append = "×"

        return currentExpression + append + sign
    }

    fun getAppendValueForOpeningBracket(currentExpression: String): String {
        return when {
            currentExpression.isEmpty() -> "("
            CalculationClass.isNumber(currentExpression.last()) || currentExpression.last() == ')' -> "×("
            CalculationClass.isDot(currentExpression.last()) -> "0×("
            else -> "("
        }
    }

}