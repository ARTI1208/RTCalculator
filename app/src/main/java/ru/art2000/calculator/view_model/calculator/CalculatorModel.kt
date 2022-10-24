package ru.art2000.calculator.view_model.calculator

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.art2000.calculator.R
import ru.art2000.calculator.model.calculator.*
import ru.art2000.calculator.model.calculator.history.*
import ru.art2000.calculator.model.calculator.parts.BinaryOperation
import ru.art2000.calculator.model.calculator.parts.PostfixOperation
import ru.art2000.calculator.view_model.ExpressionInputViewModel
import ru.art2000.calculator.view_model.ExpressionInputViewModel.Companion.zero
import ru.art2000.calculator.view_model.calculator.CalculationLexer.Companion.isFloatingPointSymbol
import ru.art2000.extensions.arch.context
import ru.art2000.extensions.arch.launchAndCollect
import ru.art2000.extensions.language.dotSafeToDouble
import ru.art2000.helpers.CalculatorPreferenceHelper
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalculatorModel @Inject constructor(
    @ApplicationContext application: Context,
    private val prefsHelper: CalculatorPreferenceHelper,
    private val historyRepository: HistoryRepository,
) : AndroidViewModel(application as Application), HistoryViewModel, ExpressionInputViewModel {

    override var decimalSeparator: Char = DecimalFormatSymbols.getInstance().decimalSeparator
        private set(value) {
            val oldValue = decimalSeparator
            if (oldValue == value) return

            field = value
            mLiveResult.update { it?.let { calculations.calculateForDisplay(it) } }
            mLiveMemory.update { calculations.calculateForDisplay(it) }
            liveExpression.update { localizeExpression(it, value) }
        }

    override val liveExpression = createLiveExpression(
        prefsHelper.lastExpression?.let { localizeExpression(it) }
    )

    override val liveInputSelection = createLiveInput()

    // TODO Use scientific formatting when come up with what to do with cos90 != 0 problem
    override val calculations: Calculations<*> = DoubleCalculations(CalculatorFormatter)

    override fun updateLocaleSpecific() {
        val symbols = DecimalFormatSymbols.getInstance()
        decimalSeparator = symbols.decimalSeparator
    }

    private val mLiveResult: MutableStateFlow<String?> = MutableStateFlow(null)
    val liveResult: StateFlow<String?> get() = mLiveResult

    private val mLiveMemory: MutableStateFlow<String> = MutableStateFlow(
        calculations.calculateForDisplay(prefsHelper.lastMemory) // apply formatting
    )
    val liveMemory: StateFlow<String> = mLiveMemory

    private val mLiveAngleType: MutableStateFlow<AngleType> = MutableStateFlow(AngleType.DEGREES)
    val liveAngleType: StateFlow<AngleType> = mLiveAngleType

    private var result: String?
        get() = mLiveResult.value
        set(value) {
            mLiveResult.value = value
        }

    private fun localizeExpression(
        expression: String,
        decimalSeparator: Char = this.decimalSeparator,
    ): String {
        return CalculationLexer.supportedDecimalSeparators.fold(expression) { acc, sep ->
            if (sep != decimalSeparator) acc.replace(sep, decimalSeparator) else acc
        }
    }

    private fun HistoryDatabaseItem.localized(): HistoryDatabaseItem {
        val localizedExpr = localizeExpression(expression)
        val localizedResult = result.let { calculations.calculateForDisplay(it) }
        return copy(expression = localizedExpr, result = localizedResult).also {
            it.id = id
        }
    }

    override val historyListItems = historyRepository.getAll().map { items ->
        var calendar: Calendar? = null
        items.fold(mutableListOf<HistoryListItem>()) { acc, historyDatabaseItem ->
            if (!isSameDay(calendar, historyDatabaseItem.date)) {
                val date = historyDatabaseItem.date.let {
                    LocalDate.ofYearDay(it[Calendar.YEAR], it[Calendar.DAY_OF_YEAR])
                }
                acc += HistoryDateItem(date)
                calendar = historyDatabaseItem.date
            }
            acc += HistoryValueItem(historyDatabaseItem.localized())
            acc
        }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), replay = 1)

    init {
        if (prefsHelper.lastExpressionWasCalculated) {
            onResult(false)
        }
        viewModelScope.launch {
            launchAndCollect(mLiveMemory) { prefsHelper.lastMemory = it  }
            launchAndCollect(liveExpression) { prefsHelper.lastExpression = it  }
            launchAndCollect(liveResult) { prefsHelper.lastExpressionWasCalculated = it != null }
        }
    }

    override fun copyHistoryItemToClipboard(item: HistoryDatabaseItem, type: Int): String {
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

    override fun updateHistoryItem(item: HistoryDatabaseItem) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.add(item)
        }
    }

    override fun removeHistoryItem(item: HistoryDatabaseItem) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.remove(item)
        }
    }

    override fun clearHistoryDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.clear()
        }
    }

    private fun saveCalculationResult(expression: String, result: String) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.add(HistoryDatabaseItem(expression, result, Calendar.getInstance()))
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
            last.isFloatingPointSymbol -> "0×"
            calculations.field.isNumberPart(last) -> "×"
            else -> ""
        }

        insertInExpression(extraAppend + sign)
    }

    fun handlePostfixUnaryOperationSign(sign: CharSequence) {

        val last = expressionLastChar ?: return

        val extraAppend = when {
            last.isFloatingPointSymbol -> zero
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

    private fun onResult(saveIfNoError: Boolean) {
        val countStr = expression
        if (countStr.isEmpty()) return

        val last = countStr.last()
        if (last.isFloatingPointSymbol || calculations.isBinaryOperationSymbol(last) || result != null) return
        var expr = addRemoveBrackets(countStr)
        if (expr.isEmpty()) {
            expr = context.getString(R.string.error)
        }
        setExpression(expr)

        val (calculated, err) = calculateAndFormatForDisplay(expr, liveAngleType.value)

        if (!err && saveIfNoError) {
            saveCalculationResult(expr, calculated)
        }
        result = calculated
    }

    fun onResult() = onResult(true)

    fun calculateAndFormatForDisplay(expression: String, angleType: AngleType): Pair<String, Boolean> {
        var countStr = calculations.calculateForDisplay(expression, angleType)

        var err = false
        countStr = when (countStr) {
            Calculations.calculationDivideByZero -> context.getString(prefsHelper.zeroDivResult)
            Calculations.calculationError -> {
                err = true
                context.getString(R.string.error)
            }
            else -> countStr
        }

        return countStr to err
    }

    fun handleMemoryOperation(operation: CharSequence) {
        var memory = mLiveMemory.value
        when (val char = operation.last()) {
            '+' -> {
                onResult()
                result?.also {
                    memory = calculations.calculateForDisplay("$memory$char$it")
                }
            }
            '-' -> {
                onResult()
                result?.also {
                    memory = calculations.calculateForDisplay("$memory$char$it")
                }
            }
            'R' -> if (calculations.field.isZeroOrClose(memory)) {
                if (result != null) result = null
                insertInExpression(memory)
            }
            'C' -> memory = "0"
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

    private fun isSameDay(calendar: Calendar?, otherCalendar: Calendar?): Boolean {
        if (calendar == null) return otherCalendar == null
        if (otherCalendar == null) return false

        return calendar[Calendar.DAY_OF_YEAR] == otherCalendar[Calendar.DAY_OF_YEAR]
                && calendar[Calendar.YEAR] == otherCalendar[Calendar.YEAR]
    }
}