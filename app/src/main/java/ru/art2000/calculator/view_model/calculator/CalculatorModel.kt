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
import ru.art2000.helpers.GeneralHelper
import ru.art2000.helpers.PrefsHelper
import kotlin.concurrent.thread

class CalculatorModel(application: Application) : AndroidViewModel(application), HistoryViewModel {

    private val historyDao = CalculatorDependencies.getHistoryDatabase(application).historyDao()

    val liveExpression: MutableLiveData<String> = MutableLiveData("0")

    private val mLiveResult: MutableLiveData<String?> = MutableLiveData(null)
    val liveResult: LiveData<String?> get() = mLiveResult

    private val mLiveMemory: MutableLiveData<Double> = MutableLiveData(0.0)
    val liveMemory: LiveData<Double> = mLiveMemory

    private val mLiveAngleType: MutableLiveData<AngleType> = MutableLiveData(AngleType.DEGREES)
    val liveAngleType: LiveData<AngleType> = mLiveAngleType

    private var expression: String
        get() = liveExpression.value!!
        set(value) {
            liveExpression.value = value
        }

    private var result: String?
        get() = mLiveResult.value
        set(value) {
            mLiveResult.value = value
        }

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

    private fun saveCalculationResult(expression: String, result: String) {
        thread {
            historyDao.insert(HistoryItem(expression, result))
        }
    }

    fun handlePrefixUnaryOperationSign(sign: CharSequence) {
        val currentExpression = expression
        if (currentExpression.isEmpty() || currentExpression == "0") {
            expression = sign.toString()
            return
        }

        val last: String = currentExpression.substring(currentExpression.lastIndex)
        val extraAppend = when {
            CalculationClass.isDot(last) -> "0×"
            CalculationClass.isNumber(last) -> "×"
            else -> ""
        }

        expression += extraAppend + sign
    }

    fun handlePostfixUnaryOperationSign(sign: CharSequence) {

        val currentExpression: String = expression
        val last = currentExpression.last()

        val extraAppend = when {
            CalculationClass.isDot(last) -> "0"
            CalculationClass.isNumber(last) -> "1"
            else -> ""
        }

        expression += extraAppend + sign
    }

    fun handleOpeningBracket() {
        if (result != null) {
            result = null
            expression = "("
            return
        }
        val currentExpression: String = expression
        val append: String = when {
            currentExpression.isEmpty() -> ""
            CalculationClass.isNumber(currentExpression.last()) || currentExpression.last() == ')' -> "×"
            CalculationClass.isDot(currentExpression.last()) -> "0×"
            else -> ""
        }
        if (currentExpression.isEmpty() || currentExpression == "0")
            expression = "$append("
        else
            expression += "$append("
    }

    fun handleClosingBracket() {
        val currentExpression: String = expression
        val ar = currentExpression.toCharArray()
        var o = 0
        var c = 0
        var lastOpenBr = -1
        var i = 0
        val exLength = ar.size
        while (i < exLength) {
            val anEx = ar[i]
            if (anEx == '(') {
                o++
                lastOpenBr = i
            }
            if (anEx == ')') c++
            i++
        }
        if (o - c > 0) {
            val exInBrs = currentExpression.substring(lastOpenBr + 1)
            val newAr = exInBrs.toCharArray()
            if (CalculationClass.signsInExpr(exInBrs) > 0
                    && (newAr[0].toString() == "-" || CalculationClass.isNumber(newAr[0]))
                    && !CalculationClass.isSign(newAr[newAr.size - 1]))
                expression += ")"
        }
    }

    fun clear() {
        expression = "0"
        result = null
    }

    fun deleteLastCharacter() {
        var inputText: String = expression
        val inpLen: Int = inputText.length
        val last = inputText.last()

        val preLast = if (inpLen > 1) inputText[inputText.lastIndex] else '!'

        inputText = if ((last == '.' || last == ',') && preLast == '0') {
            inputText.substring(0, inpLen - 2)
        } else {
            inputText.substring(0, inpLen - 1)
        }

        when {
            result != null -> {
                expression = "0"
                result = null
            }
            inpLen == 1 -> expression = "0"
            else -> expression = inputText
        }
    }

    fun appendBinaryOperationSign(sign: CharSequence) {
        var toAdd: String = sign.toString()

        val inputText: String = expression
        val inpLen: Int = inputText.length

        val last = inputText.substring(inpLen - 1, inpLen)
        var prelast = ""
        if (inpLen > 1) prelast = inputText.substring(inpLen - 2, inpLen - 1)
        if (result != null) {
            if (result?.toDoubleOrNull() != null) {
                toAdd = result + toAdd
                expression = toAdd
                result = null
                return
            }
            result = null
        }
        if (!(inputText == "0" || inputText == "-")) {
            if (CalculationClass.isSign(last) && toAdd != "-") {
                val copied = inputText.substring(0, inpLen - 1) + toAdd
                expression = copied
            } else if (last == "." || last == ",") {
                toAdd = "0$toAdd"
                expression += toAdd
            } else if (toAdd == "-" && last == "-") {
                if (CalculationClass.isSign(prelast)) expression = inputText.substring(0, inpLen - 1)
            } else if (toAdd == "-" && CalculationClass.isSign(last)) {
                expression += "(-"
            } else {
                expression += toAdd
            }
        } else if (toAdd == "-") expression = toAdd
    }

    fun onResult() {
        var countStr: String = expression
        var err = false
        val countLen = countStr.length
        val last = countStr.substring(countLen - 1, countLen)
        if (last == "." || last == "," || CalculationClass.isSign(last) || result != null) return
        var expr = CalculationClass.addRemoveBrackets(countStr)
        if (expr.isEmpty()) {
            expr = context.getString(R.string.error)
        }
        expression = expr
        countStr = CalculationClass.calculateStr(expr)
        when (countStr) {
            "zero" -> countStr = context.getString(PrefsHelper.getZeroDivResult())
            "error" -> {
                countStr = context.getString(R.string.error)
                err = true
            }
        }
        if (!err) {
            saveCalculationResult(expr, countStr)
        }
        result = countStr
    }

    fun handleMemoryOperation(operation: CharSequence) {
        when (operation.last()) {
            '+' -> {
                onResult()
                CalculationClass.memory += result?.toDoubleOrNull() ?: 0.0
            }
            '-' -> {
                onResult()
                CalculationClass.memory -= result?.toDoubleOrNull() ?: 0.0
            }
            'R' -> if (CalculationClass.memory != 0.0) {
                if (result != null) {
                    result = null
                }
                expression = GeneralHelper.resultNumberFormat.format(CalculationClass.memory)
            }
            'C' -> CalculationClass.memory = 0.0
        }

        mLiveMemory.value = CalculationClass.memory
    }

    fun handleConstant(constant: CharSequence) {
        val last = expression.last()
        val append = when {
            CalculationClass.isNumber(last) -> "×$constant"
            CalculationClass.isDot(last) -> "0×$constant"
            else -> constant
        }
        expression += append
    }

    fun handleNumber(number: CharSequence) {
        var toAdd: String = number.toString()

        val inputText: String = expression

        val last = inputText.last()
        if (result != null) {
            result = null
            expression = toAdd
            return
        }

        val zeroStr = "0"
        if (toAdd == zeroStr && CalculationClass.isSign(last)) {
            toAdd = "0,"
        }
        if (inputText == zeroStr)
            expression = toAdd
        else
            expression += toAdd
    }

    fun handleDot() {
        var toAdd = ","
        var lastSign = 0

        val inputText: String = expression
        val inpLen: Int = inputText.length

        val last = inputText.last()
        val zeroStr = "0"
        if (result != null) {
            result = null
            expression = "0,"
            return
        }
        var i: Int = inpLen - 1
        while (i > 0) {
            if (CalculationClass.isSign(inputText.toCharArray()[i].toString())) {
                lastSign = i
                break
            }
            i--
        }
        val lNum = inputText.substring(lastSign, inpLen)
        if (lNum.contains(".") || lNum.contains(",")) return else if (CalculationClass.isSign(last) && inputText != zeroStr) toAdd = "0,"

        expression += toAdd
    }

    fun changeAngleType(): String {
        CalculationClass.radians = CalculationClass.radians xor true
        mLiveAngleType.value = if (CalculationClass.radians) AngleType.RADIANS else AngleType.DEGREES
        return (if (CalculationClass.radians) AngleType.DEGREES else AngleType.RADIANS).toString()
    }
}