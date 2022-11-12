package ru.art2000.calculator.calculator.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.art2000.calculator.calculator.computation.*
import ru.art2000.calculator.calculator.computation.CalculationLexer.Companion.isFloatingPointSymbol
import ru.art2000.calculator.calculator.computation.parts.BinaryOperation
import ru.art2000.calculator.calculator.computation.parts.PostfixOperation
import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.model.HistoryContentItem
import ru.art2000.calculator.calculator.model.HistoryValueItem
import ru.art2000.calculator.calculator.preferences.CalculatorPreferenceHelper
import ru.art2000.calculator.calculator.repo.HistoryRepository
import ru.art2000.extensions.kt.launchAndCollect
import ru.art2000.extensions.strings.dotSafeToDouble
import ru.art2000.calculator.calculator.computation.localizeExpression as computationLocalizeExpression

internal class CalculatorCommonModel(
    prefsHelper: CalculatorPreferenceHelper,
    private val historyRepository: HistoryRepository,
    private val viewModelScope: CoroutineScope,
    private val copyText: (String) -> Unit,
    private val getSystemDecimalSeparator: () -> Char,
    private val getDivideByZeroResult: () -> String,
    private val getErrorString: () -> String,
    formatter: CalculationNumberFormatter<Double>,
): HistoryViewModel, ExpressionInputViewModel {

    override var decimalSeparator: Char = getSystemDecimalSeparator()
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
    override val calculations: Calculations<*> = DoubleCalculations(formatter)

    override fun updateLocaleSpecific() {
        decimalSeparator = getSystemDecimalSeparator()
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
    ) = computationLocalizeExpression(expression, decimalSeparator)

    private fun HistoryValueItem.localized(): HistoryValueItem {
        val localizedExpr = localizeExpression(expression)
        val localizedResult = result.let { calculations.calculateForDisplay(it) }
        return copy(expression = localizedExpr, result = localizedResult)
    }

    override val historyListItems = historyRepository.getAll().map { items ->
        items.map {
            (it as? HistoryValueItem)?.localized() ?: it
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

    override fun copyHistoryItemToClipboard(
        item: HistoryValueItem,
        type: HistoryViewModel.CopyMode
    ) = when (type) {
        HistoryViewModel.CopyMode.EXPRESSION -> item.expression
        HistoryViewModel.CopyMode.RESULT -> item.result
        HistoryViewModel.CopyMode.ALL -> item.fullExpression
    }.also { copyText(it) }

    override fun updateHistoryItem(item: HistoryValueItem) {
        viewModelScope.launch(Dispatchers.Default) {
            historyRepository.update(item)
        }
    }

    override fun removeHistoryItem(item: HistoryValueItem) {
        viewModelScope.launch(Dispatchers.Default) {
            historyRepository.remove(item)
        }
    }

    override fun clearHistoryDatabase() {
        viewModelScope.launch(Dispatchers.Default) {
            historyRepository.clear()
        }
    }

    private fun saveCalculationResult(expression: String, result: String) {
        viewModelScope.launch(Dispatchers.Default) {
            historyRepository.add(HistoryContentItem(expression, result, null))
        }
    }

    fun handlePrefixUnaryOperationSign(sign: CharSequence) {
        val currentExpression = expression
        if (currentExpression.isEmpty() || currentExpression == ExpressionInputViewModel.zero) {
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
            last.isFloatingPointSymbol -> ExpressionInputViewModel.zero
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
        if (currentExpression.isEmpty() || currentExpression == ExpressionInputViewModel.zero)
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
            toAdd += ExpressionInputViewModel.zero
        }

        if (calculations.startsWithOperation<BinaryOperation<Double>>(textAfter) ||
            calculations.startsWithOperation<PostfixOperation<Double>>(textAfter)) {
            toAdd += "1"
        }

        if (textBefore.isEmpty() || textBefore == ExpressionInputViewModel.zero || textBefore == "-") {
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
            toAdd = "${ExpressionInputViewModel.zero}$toAdd"
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
            expr = getErrorString()
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
        val result = calculations.calculateForDisplay(expression, angleType)

        val err = result == Calculations.calculationError
        val display = when (result) {
            Calculations.calculationDivideByZero -> getDivideByZeroResult()
            Calculations.calculationError -> getErrorString()
            else -> result
        }

        return display to err
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

}