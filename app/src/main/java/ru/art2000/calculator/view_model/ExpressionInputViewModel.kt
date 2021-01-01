package ru.art2000.calculator.view_model

import androidx.lifecycle.MutableLiveData
import ru.art2000.calculator.view_model.calculator.CalculationClass

interface ExpressionInputViewModel {

    companion object {

        val Char.isFloatingPointSymbol: Boolean
            get() = this == '.' || this == ','

        private val floatingPointSymbolString: String
            get() = ","

        const val zero = "0"

        const val one = "1"

        val floatingPointZero = zero + floatingPointSymbolString
    }

    val liveExpression: MutableLiveData<String>

    var expression: String
        get() = liveExpression.value!!
        set(value) {
            liveExpression.value = value
        }

    val liveInputSelection: MutableLiveData<Pair<Int, Int>>

    var inputSelection: Pair<Int, Int>
        get() = liveInputSelection.value!!
        set(value) {
            liveInputSelection.value = value
        }

    fun clear() {
        setExpression("")
    }

    fun deleteLastCharacter() {

        if (inputSelection.first != inputSelection.second) {
            insertInExpression("")
            return
        }

        if (expression.length == 1 && inputSelection.second > 0) {
            clear()
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
            if (CalculationClass.isBinaryOperationSymbol(last)) {
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

        var toAdd = floatingPointSymbolString
        var lastSign = 0

        val inputText: String = expression
        val inpLen: Int = inputText.length

        var i: Int = inpLen - 1
        while (i > 0) {
            if (CalculationClass.isBinaryOperationSymbol(inputText[i].toString())) {
                lastSign = i
                break
            }
            i--
        }

        val lNum = inputText.substring(lastSign, inpLen)

        if (lNum.contains(".") || lNum.contains(",")) return

        if (CalculationClass.isBinaryOperationSymbol(last)) {
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

    fun createExpressionLiveData(initialValue: String? = null): MutableLiveData<String> {
        return MutableLiveData(initialValue ?: "")
    }

    /*
     * Moves selection caret to initial expression end. Call only after
     * liveExpression is assigned a value
     */
    fun createInputLiveData(): MutableLiveData<Pair<Int, Int>> {
        val selection = expression.length
        return MutableLiveData(selection to selection)
    }
}