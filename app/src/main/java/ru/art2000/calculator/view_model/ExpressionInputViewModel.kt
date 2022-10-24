package ru.art2000.calculator.view_model

import kotlinx.coroutines.flow.MutableStateFlow
import ru.art2000.calculator.view_model.calculator.CalculationLexer.Companion.isFloatingPointSymbol
import ru.art2000.calculator.view_model.calculator.Calculations
import ru.art2000.calculator.view_model.calculator.isBinaryOperationSymbol
import java.text.DecimalFormatSymbols

interface ExpressionInputViewModel {

    companion object {

        const val zero = "0"

        const val one = "1"
    }

    val liveExpression: MutableStateFlow<String>

    var expression: String
        get() = liveExpression.value
        set(value) {
            liveExpression.value = value
        }

    val liveInputSelection: MutableStateFlow<Pair<Int, Int>>

    var inputSelection: Pair<Int, Int>
        get() = liveInputSelection.value
        set(value) {
            liveInputSelection.value = value
        }

    val decimalSeparator: Char
        get() = DecimalFormatSymbols.getInstance().decimalSeparator

    val floatingPointZero
        get() = zero + decimalSeparator

    val calculations: Calculations<*>

    fun updateLocaleSpecific() {}

    fun clearInput() {
        setExpression("")
    }

    fun deleteLastCharacter() {

        if (inputSelection.first != inputSelection.second) {
            insertInExpression("")
            return
        }

        if (expression.length == 1 && inputSelection.second > 0) {
            clearInput()
            return
        }

        val last = expressionLastChar ?: return

        val preLast = if (inputSelection.second >= 2) expression[inputSelection.second - 2] else null
        val dropCount = if (last.isFloatingPointSymbol && preLast == '0') 2 else 1

        replaceExpressionPart("", inputSelection.second - dropCount, inputSelection.second)
    }

    fun handleNumber(number: CharSequence) {
        var toAdd: String = number.toString()

        val inputText: String = expression

        val last = expressionLastChar

        if (toAdd == zero) {
            if (last == null) {
                if (inputText.isEmpty()) {
                    setExpression(floatingPointZero)
                }
                return
            }
            if (calculations.isBinaryOperationSymbol(last)) {
                toAdd = floatingPointZero
            }
        }
        if (inputText == zero)
            setExpression(toAdd)
        else
            insertInExpression(toAdd)
    }

    fun handleFloatingPointSymbol() {
        val last = expressionLastChar ?: return

        var toAdd = decimalSeparator.toString()
        var lastSign = 0

        val inputText: String = expression
        val inpLen: Int = inputText.length

        var i: Int = inpLen - 1
        while (i > 0) {
            if (calculations.isBinaryOperationSymbol(inputText[i].toString())) {
                lastSign = i
                break
            }
            i--
        }

        val lNum = inputText.substring(lastSign, inpLen)

        if (lNum.contains(".") || lNum.contains(",")) return

        if (calculations.isBinaryOperationSymbol(last)) {
            toAdd = floatingPointZero
        }

        insertInExpression(toAdd)
    }

    val expressionLastChar: Char?
        get() = if (inputSelection.second >= 1) expression[inputSelection.second - 1] else null

    fun setExpression(charSequence: CharSequence) {
        expression = charSequence.toString()
        inputSelection = expression.length to expression.length
    }

    fun insertInExpression(charSequence: CharSequence) {
        replaceExpressionPart(charSequence, inputSelection.first, inputSelection.second)
    }

    fun replaceExpressionPart(charSequence: CharSequence, from: Int = 0, to: Int = expression.length) {
        expression = expression.substring(0, from) + charSequence + expression.substring(to)

        val newSelection = from + charSequence.length
        inputSelection = newSelection to newSelection
    }

    fun createLiveExpression(initialValue: String? = null): MutableStateFlow<String> {
        return MutableStateFlow(initialValue ?: "")
    }

    /*
     * Moves selection caret to initial expression end. Call only after
     * liveExpression is assigned a value
     */
    fun createLiveInput(): MutableStateFlow<Pair<Int, Int>> {
        val selection = expression.length
        return MutableStateFlow(selection to selection)
    }
}