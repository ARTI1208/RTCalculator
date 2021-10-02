package ru.art2000.calculator.view_model.calculator

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.art2000.calculator.R
import ru.art2000.calculator.model.calculator.*
import ru.art2000.calculator.model.calculator.numbers.CalculationNumber
import ru.art2000.calculator.model.calculator.parts.BinaryOperation
import ru.art2000.calculator.model.calculator.parts.PostfixOperation
import ru.art2000.calculator.view_model.ExpressionInputViewModel
import ru.art2000.calculator.view_model.ExpressionInputViewModel.Companion.floatingPointZero
import ru.art2000.calculator.view_model.ExpressionInputViewModel.Companion.isFloatingPointSymbol
import ru.art2000.calculator.view_model.ExpressionInputViewModel.Companion.zero
import ru.art2000.extensions.arch.context
import ru.art2000.extensions.language.dotSafeToDouble
import ru.art2000.helpers.GeneralHelper
import ru.art2000.helpers.PrefsHelper
import kotlin.concurrent.thread

class CalculatorModel(
        application: Application
) : AndroidViewModel(application), HistoryViewModel, ExpressionInputViewModel {

    private val historyDao = CalculatorDependencies.getHistoryDatabase(application).historyDao()

    override val liveExpression: MutableLiveData<String> = createExpressionLiveData()

    override val liveInputSelection: MutableLiveData<Pair<Int, Int>> = createInputLiveData()

    private val mLiveResult: MutableLiveData<String?> = MutableLiveData(null)
    val liveResult: LiveData<String?> get() = mLiveResult

    private val mLiveMemory: MutableLiveData<Double> = MutableLiveData(0.0)
    val liveMemory: LiveData<Double> = mLiveMemory

    private val mLiveAngleType: MutableLiveData<AngleType> = MutableLiveData(AngleType.DEGREES)
    val liveAngleType: LiveData<AngleType> = mLiveAngleType

    private var result: String?
        get() = mLiveResult.value
        set(value) {
            mLiveResult.value = value
        }

    // TODO Use scientific formatting when come up with what to do with cos90 != 0 problem
    override val calculations: Calculations<Double> = DoubleCalculations(CalculatorFormatter)

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
        if (currentExpression.isEmpty() || currentExpression == zero) {
            setExpression(sign)
            return
        }

        val last = expressionLastChar

        val extraAppend = when {
            last == null -> ""
            calculations.field.isFloatingPointSymbol(last) -> "0×"
            calculations.field.isNumberPart(last) -> "×"
            else -> ""
        }

        insertInExpression(extraAppend + sign)
    }

    fun handlePostfixUnaryOperationSign(sign: CharSequence) {

        val last = expressionLastChar ?: return

        val extraAppend = when {
            calculations.field.isFloatingPointSymbol(last) -> zero
            else -> ""
        }

        insertInExpression(extraAppend + sign)
    }

    fun handleOpeningBracket() {
        if (result != null) {
            result = null
            setExpression("(")
            return
        }
        val currentExpression: String = expression

        val last = expressionLastChar ?: kotlin.run {
            setExpression("(")
            return
        }

        val append: String = when {
            currentExpression.isEmpty() -> ""
            calculations.field.isNumberPart(last) || last == ')' -> "×"
            last.isFloatingPointSymbol -> "0×"
            else -> ""
        }
        if (currentExpression.isEmpty() || currentExpression == zero)
            setExpression("$append(")
        else
            insertInExpression("$append(")
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
            if (calculations.hasSignsInExpression(exInBrs)
                    && (newAr[0].toString() == "-" || calculations.field.isNumberPart(newAr[0]))
                    && !calculations.isBinaryOperationSymbol(newAr[newAr.size - 1]))
                insertInExpression(")")
        }
    }

    override fun clearInput() {
        super.clearInput()
        result = null
    }

    override fun deleteLastCharacter() {
        if (result != null) {
            clearInput()
            return
        }

        super.deleteLastCharacter()
    }

    fun appendBinaryOperationSign(sign: CharSequence) {

        var toAdd: String = sign.toString()

        val previousResult = result
        if (previousResult?.dotSafeToDouble() != null) { // expression = result + sign, remove result
            setExpression(previousResult + toAdd)
            result = null
            return
        } else {
            result = null
        }

        val last = expressionLastChar ?: return
        val textBefore = expression.substring(0, inputSelection.first)
        val textAfter = expression.substring(inputSelection.second)

        if (textAfter.firstOrNull()?.isFloatingPointSymbol == true) {
            toAdd += zero
        }

        if (calculations.startsWithOperation<BinaryOperation<Double>>(textAfter) ||
                calculations.startsWithOperation<PostfixOperation<Double>>(textAfter)) {
            toAdd += "1"
        }

        if (textBefore.isEmpty() || textBefore == zero || textBefore == "-") {
            if (toAdd == "-") insertInExpression(toAdd)
            return
        }

        if (calculations.isBinaryOperationSymbol(last)) {
            if (toAdd == "-") {
                if (last == '-') return

                insertInExpression("(-")
            } else {
                val result = textBefore.dropLast(1) + toAdd
                replaceExpressionPart(result, 0, textBefore.length)
            }
        } else if (last.isFloatingPointSymbol) {
            toAdd = "$zero$toAdd"
            insertInExpression(toAdd)
        } else {
            insertInExpression(toAdd)
        }
    }

    fun onResult() {
        var countStr: String = expression
        if (countStr.isEmpty()) return

        var err = false
        val last = countStr.last()
        if (last.isFloatingPointSymbol || calculations.isBinaryOperationSymbol(last) || result != null) return
        var expr = addRemoveBrackets(countStr)
        if (expr.isEmpty()) {
            expr = context.getString(R.string.error)
        }
        setExpression(expr)

        countStr = calculations.calculateForDisplay(expr, liveAngleType.value!!)

        when (countStr) {
            Calculations.calculationDivideByZero -> countStr = context.getString(PrefsHelper.getZeroDivResult())
            Calculations.calculationError -> {
                countStr = context.getString(R.string.error)
                err = true
            }
        }
        if (!err) {
            saveCalculationResult(expr, countStr)
        }
        result = countStr
    }

    fun formatNumberForDisplay(calculationNumber: CalculationNumber<Double>?): String {
        return when {
            calculationNumber == null -> context.getString(PrefsHelper.getZeroDivResult())
            calculationNumber.isInfinite -> context.getString(R.string.error)
            else -> calculations.formatter.format(calculationNumber)
        }
    }

    fun handleMemoryOperation(operation: CharSequence) {
        var memory = mLiveMemory.value ?: 0.0
        when (operation.last()) {
            '+' -> {
                onResult()
                memory += result?.dotSafeToDouble() ?: 0.0
            }
            '-' -> {
                onResult()
                memory -= result?.dotSafeToDouble() ?: 0.0
            }
            'R' -> if (memory != 0.0) {
                if (result != null) result = null
                val memoryValue = GeneralHelper.resultNumberFormat.format(memory)
                insertInExpression(memoryValue)
            }
            'C' -> memory = 0.0
        }

        mLiveMemory.value = memory
    }

    fun handleConstant(constant: CharSequence) {
        val last = expressionLastChar
        val append = when {
            last == null -> constant
            calculations.field.isNumberPart(last) -> "×$constant"
            last.isFloatingPointSymbol -> "0×$constant"
            else -> constant
        }
        insertInExpression(append)
    }

    override fun handleNumber(number: CharSequence) {
        if (result != null) {
            result = null
            setExpression(number.toString())
            return
        }

        super.handleNumber(number)
    }

    override fun handleFloatingPointSymbol() {
        if (result != null) {
            result = null
            setExpression(floatingPointZero)
            return
        }

        super.handleFloatingPointSymbol()
    }

    fun clearResult() {
        result = null
    }

    private val AngleType?.reversed: AngleType
        get() = when (this) {
            AngleType.DEGREES -> AngleType.RADIANS
            AngleType.RADIANS -> AngleType.DEGREES
            null -> AngleType.DEGREES
        }

    fun changeAngleType(): String {

        mLiveAngleType.value = mLiveAngleType.value.reversed

        return mLiveAngleType.value.reversed.toString()
    }
}