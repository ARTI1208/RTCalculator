package ru.art2000.calculator.view.calculator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.CalculatorPage1Binding
import ru.art2000.calculator.databinding.CalculatorPage2Binding
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.view_model.calculator.CalculationClass
import ru.art2000.calculator.view_model.calculator.CalculatorModel
import ru.art2000.helpers.GeneralHelper
import ru.art2000.helpers.PrefsHelper

class CalculatorButtonsPagerAdapter(
        private val mContext: Context,
        private val model: CalculatorModel,
) : PagerAdapter() {

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = if (position == 1) {
            val page2Binding = CalculatorPage2Binding.inflate(
                    LayoutInflater.from(mContext), container, false
            )
            setButtonsClickListener(page2Binding)
            page2Binding.root
        } else {
            val page1Binding = CalculatorPage1Binding.inflate(
                    LayoutInflater.from(mContext), container, false
            )
            setButtonsClickListener(page1Binding)
            page1Binding.root
        }

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }


    //=========================================
    private var expression: String 
        get() = model.currentExpression.value!!
        set(value) { model.currentExpression.value = value }

    private var result: String?
        get() = model.currentResult.value
        set(value) { model.currentResult.value = value }

    private fun setClearButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        val clearButtons = arrayOf(page1Binding.buttonClear, page1Binding.buttonDel)
        for (clearButton in clearButtons) {
            clearButton.setOnClickListener(::onClearButtonClick)
        }
    }

    private fun setSignButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        val buttons = arrayOf(
                page1Binding.buttonPlus, page1Binding.buttonMinus,
                page1Binding.buttonMult, page1Binding.buttonDiv
        )
        for (button in buttons) {
            button.setOnClickListener { onBinaryOperationSignButtonClick(button) }
        }
    }

    private fun setSignButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonRDiv, page2Binding.buttonMod, page2Binding.buttonPow
        )
        for (button in buttons) {
            button.setOnClickListener { onBinaryOperationSignButtonClick(button) }
        }
    }

    private fun setBracketButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        page1Binding.buttonLeftBracket.setOnClickListener { onOBCClick() }
        page1Binding.buttonRightBracket.setOnClickListener { onCBCClick() }
    }

    private fun setNumberButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        val buttons = arrayOf(
                page1Binding.button0, page1Binding.button1,
                page1Binding.button2, page1Binding.button3,
                page1Binding.button4, page1Binding.button5,
                page1Binding.button6, page1Binding.button7,
                page1Binding.button8, page1Binding.button9)
        for (button in buttons) {
            button.setOnClickListener(::onBtnClick)
        }
    }

    private fun setDotButtonClickListener(page1Binding: CalculatorPage1Binding) {
        page1Binding.buttonDot.setOnClickListener(::onBtnClick)
    }

    private fun setEqualsButtonClickListener(page1Binding: CalculatorPage1Binding) {
        page1Binding.buttonEQ.setOnClickListener { onResult() }
    }

    private fun setPreUnarySignButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonSin, page2Binding.buttonCos,
                page2Binding.buttonTg, page2Binding.buttonCtg,
                page2Binding.buttonLg, page2Binding.buttonLn,
                page2Binding.buttonSqrt
        )
        for (button in buttons) {
            button.setOnClickListener(::onPreUnarySignClick)
        }
    }

    private fun setPostUnarySignButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonPercent, page2Binding.buttonFactorial)
        for (button in buttons) {
            button.setOnClickListener(::onAfterUnarySignClick)
        }
    }

    private fun setMemoryButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonMPlus, page2Binding.buttonMMinus,
                page2Binding.buttonMClear, page2Binding.buttonMResult)
        for (button in buttons) {
            button.setOnClickListener(::onMemoryBtnClick)
        }
    }

    private fun setConstantButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonPi, page2Binding.buttonEulerNumber, page2Binding.buttonGoldenRatio)
        for (button in buttons) {
            button.setOnClickListener(::onConstantBtnClick)
        }
    }

    private fun setAngleTypeButtonClickListener(page2Binding: CalculatorPage2Binding) {
        page2Binding.buttonDEGRAD.setOnClickListener(::onAngleTypeChange)
    }

    private fun setButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        setClearButtonsClickListener(page1Binding)
        setSignButtonsClickListener(page1Binding)
        setBracketButtonsClickListener(page1Binding)
        setNumberButtonsClickListener(page1Binding)
        setDotButtonClickListener(page1Binding)
        setEqualsButtonClickListener(page1Binding)
    }

    private fun setButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        setSignButtonsClickListener(page2Binding)
        setPreUnarySignButtonsClickListener(page2Binding)
        setPostUnarySignButtonsClickListener(page2Binding)
        setMemoryButtonsClickListener(page2Binding)
        setConstantButtonsClickListener(page2Binding)
        setAngleTypeButtonClickListener(page2Binding)
    }

    private fun onClearButtonClick(v: View) {
        if (v.id == R.id.buttonClear) {
            clearInput()
        } else if (v.id == R.id.buttonDel) {
            val inputText: String = expression
            val inpLen: Int = inputText.length
            val last = inputText.substring(inpLen - 1, inpLen)
            var prelast = "-1"
            if (inpLen > 1) prelast = inputText.substring(inpLen - 2, inpLen - 1)
            val newText: String
            newText = if ((last == "." || last == ",") && prelast == "0") {
                inputText.substring(0, inpLen - 2)
            } else {
                inputText.substring(0, inpLen - 1)
            }
            expression = newText
            if (inpLen == 1) expression = "0"
            if (result != null) {
                expression = "0"
                result = null
            }
        }
    }

    private fun onPreUnarySignClick(button: View) {
        val buttonText = (button as Button).text.toString()
        val ex: String = expression
        expression = model.getNewExpressionForPreUnarySign(ex, buttonText)
    }

    private fun onAfterUnarySignClick(button: View) {
        val buttonText = (button as Button).text.toString()
        val ex: String = expression
        val last = ex.substring(ex.length - 1)
        var append = ""
        if (CalculationClass.isDot(last)) append += "0" else if (CalculationClass.isSign(last)) append += "1"
        append += buttonText
        expression += append
    }

    private fun onOBCClick() {
        if (result != null) {
            result = null
            expression = "("
            return
        }
        val ex: String = expression
        val append: String = model.getAppendValueForOpeningBracket(ex)
        if (ex.isEmpty() || ex == "0") expression = append else expression += append
    }

    private fun onCBCClick() {
        val ex: String = expression
        val ar = ex.toCharArray()
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
            val exInBrs = ex.substring(lastOpenBr + 1)
            val newAr = exInBrs.toCharArray()
            if (CalculationClass.signsInExpr(exInBrs) > 0 && (newAr[0].toString() == "-" || CalculationClass.isNumber(newAr[0])) && !CalculationClass.isSign(newAr[newAr.size - 1])) expression += ")"
        }
    }

    private fun onBinaryOperationSignButtonClick(v: Button) {
        var toAdd: String
        
        val inputText: String = expression
        val inpLen: Int = inputText.length
        
        val last = inputText.substring(inpLen - 1, inpLen)
        var prelast = ""
        if (inpLen > 1) prelast = inputText.substring(inpLen - 2, inpLen - 1)
        toAdd = when (v.id) {
            R.id.buttonRDiv -> "/"
            R.id.buttonMod -> ":"
            else -> v.text.toString()
        }
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

    private fun onBtnClick(v: View) {
        var toAdd: String
        var lastSign = 0
        val buttonText = (v as Button).text.toString()
        val inputText: String = expression
        val inpLen: Int = inputText.length
        
        val last = inputText.substring(inpLen - 1, inpLen)
        val zeroStr = "0"
        toAdd = if (buttonText == ".") "," else buttonText
        if (result != null) {
            result = null
            if (buttonText == "." || buttonText == ",") toAdd = "0,"
            expression = toAdd
            return
        }
        when (buttonText) {
            ",", "." -> {
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
            }
            "0" -> if (CalculationClass.isSign(last)) toAdd = "0,"
        }
        if (expression == zeroStr && v.getId() != R.id.buttonDot) expression = ""
        expression += toAdd
    }

    private fun onResult() {
        var countStr: String = expression
        var err = false
        val countLen = countStr.length
        val last = countStr.substring(countLen - 1, countLen)
        if (last == "." || last == "," || CalculationClass.isSign(last) || result != null) return
        var expr = CalculationClass.addRemoveBrackets(countStr)
        if (expr.isEmpty()) {
            expr = mContext.getString(R.string.error)
        }
        expression = expr
        countStr = CalculationClass.calculateStr(expr)
        when (countStr) {
            "zero" -> countStr = mContext.getString(PrefsHelper.getZeroDivResult())
            "error" -> {
                countStr = mContext.getString(R.string.error)
                err = true
            }
        }
        if (!err) {
            model.saveCalculationResult(expr, countStr)
        }
        result = countStr
    }

    private fun clearInput() {
        result = null
        expression = "0"
    }

    private fun onAngleTypeChange(button: View) {
        CalculationClass.radians = CalculationClass.radians xor true
        (button as Button).text = if (!CalculationClass.radians) "RAD" else "DEG"
        model.currentAngleType.value = if (CalculationClass.radians) AngleType.RADIANS else AngleType.DEGREES
    }

    private fun onConstantBtnClick(button: View) {
        val text = (button as Button).text.toString()
        val ex: String = expression
        val last = ex.substring(ex.length - 1)
        val append = when {
            CalculationClass.isNumber(last) -> "×$text"
            CalculationClass.isDot(last) -> "0×$text"
            else -> text
        }
        expression += append
    }

    private fun onMemoryBtnClick(button: View) {
        val text = (button as Button).text.toString()
        when (text.substring(text.length - 1)) {
            "+" -> {
                onResult()
                CalculationClass.memory += result?.toDoubleOrNull() ?: 0.0
            }
            "-" -> {
                onResult()
                CalculationClass.memory -= result?.toDoubleOrNull() ?: 0.0
            }
            "R" -> if (CalculationClass.memory != 0.0) {
                if (result != null) { result = null }
                expression = GeneralHelper.resultNumberFormat.format(CalculationClass.memory)
            }
            "C" -> CalculationClass.memory = 0.0
        }

        model.currentMemory.value = CalculationClass.memory
    }

}