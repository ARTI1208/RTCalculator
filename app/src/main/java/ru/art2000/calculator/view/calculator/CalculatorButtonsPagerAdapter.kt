package ru.art2000.calculator.view.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.CalculatorExpertiseViewBinding
import ru.art2000.calculator.databinding.CalculatorPage1Binding
import ru.art2000.calculator.databinding.CalculatorPage2Binding
import ru.art2000.calculator.view_model.calculator.CalculationClass
import ru.art2000.calculator.view_model.calculator.CalculatorModel
import ru.art2000.helpers.AndroidHelper
import java.util.*

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
        page1Binding.buttonLeftBracket.setOnClickListener { model.handleOpeningBracket() }
        page1Binding.buttonRightBracket.setOnClickListener { model.handleClosingBracket() }
    }

    private fun setNumberButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        val buttons = arrayOf(
                page1Binding.button0, page1Binding.button1,
                page1Binding.button2, page1Binding.button3,
                page1Binding.button4, page1Binding.button5,
                page1Binding.button6, page1Binding.button7,
                page1Binding.button8, page1Binding.button9)
        for (button in buttons) {
            button.setOnClickListener { model.handleNumber(button.text) }
        }
    }

    private fun setDotButtonClickListener(page1Binding: CalculatorPage1Binding) {
        page1Binding.buttonDot.setOnClickListener { model.handleFloatingPointSymbol() }
    }

    private fun setEqualsButtonClickListener(page1Binding: CalculatorPage1Binding) {
        page1Binding.buttonEQ.setOnClickListener { model.onResult() }
    }

    @SuppressLint("RestrictedApi", "VisibleForTests")
    private fun setEqualsButtonLongClickListener(page1Binding: CalculatorPage1Binding) {
        page1Binding.buttonEQ.setOnLongClickListener {
            if (model.expression.isEmpty()) return@setOnLongClickListener false

            val bottomSheetDialog = BottomSheetDialog(mContext)
            bottomSheetDialog.behavior.disableShapeAnimations()

            val binding = CalculatorExpertiseViewBinding.inflate(LayoutInflater.from(mContext))
            bottomSheetDialog.setContentView(binding.root)
            bottomSheetDialog.show()

            binding.expertiseIo.tvInput.isFocusable = false
            binding.expertiseIo.tvResult.visibility = View.VISIBLE

            val expr = CalculationClass.addRemoveBrackets(model.expression)

            binding.expertiseIo.tvInput.setText(expr)

            val (lexemes, lexerTime) = model.debugLexemes(expr)
            val (computable, parserTime) = model.debugParsing(lexemes)
            val (result, computeTime) = model.debugComputation(computable)

            binding.expertiseIo.tvResult.text = result

            binding.lexemesList.apply {
                adapter = LexemeListAdapter(mContext, lexemes)
                layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
            }

            binding.lexerTime.text = mContext.getString(R.string.debug_lexer_time, lexerTime)
            binding.parserTime.text = mContext.getString(R.string.debug_parser_time, parserTime)
            binding.computeTime.text = mContext.getString(R.string.debug_compute_time, computeTime)

            val ioBackground = ContextCompat.getDrawable(mContext, R.drawable.calculator_input_bg_debug) as GradientDrawable
            val inputBackgroundColor = AndroidHelper.getColorAttribute(mContext, R.attr.calculatorInputBackground)
            ioBackground.setColor(inputBackgroundColor)

            binding.expertiseIoWrapper.background = ioBackground

            return@setOnLongClickListener true
        }
    }

    private fun setPreUnarySignButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonSin, page2Binding.buttonCos,
                page2Binding.buttonTg, page2Binding.buttonCtg,
                page2Binding.buttonLg, page2Binding.buttonLn,
                page2Binding.buttonSqrt
        )
        for (button in buttons) {
            button.setOnClickListener { model.handlePrefixUnaryOperationSign(button.text) }
        }
    }

    private fun setPostUnarySignButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonPercent, page2Binding.buttonFactorial)
        for (button in buttons) {
            button.setOnClickListener { model.handlePostfixUnaryOperationSign(button.text) }
        }
    }

    private fun setMemoryButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonMPlus, page2Binding.buttonMMinus,
                page2Binding.buttonMClear, page2Binding.buttonMResult)
        for (button in buttons) {
            button.setOnClickListener { model.handleMemoryOperation(button.text) }
        }
    }

    private fun setConstantButtonsClickListener(page2Binding: CalculatorPage2Binding) {
        val buttons = arrayOf(
                page2Binding.buttonPi, page2Binding.buttonEulerNumber, page2Binding.buttonGoldenRatio)
        for (button in buttons) {
            button.setOnClickListener { model.handleConstant(button.text) }
        }
    }

    private fun setAngleTypeButtonClickListener(page2Binding: CalculatorPage2Binding) {
        page2Binding.buttonDEGRAD.setOnClickListener {
            page2Binding.buttonDEGRAD.text = model.changeAngleType().toUpperCase(Locale.getDefault())
        }
    }

    private fun setButtonsClickListener(page1Binding: CalculatorPage1Binding) {
        setClearButtonsClickListener(page1Binding)
        setSignButtonsClickListener(page1Binding)
        setBracketButtonsClickListener(page1Binding)
        setNumberButtonsClickListener(page1Binding)
        setDotButtonClickListener(page1Binding)
        setEqualsButtonClickListener(page1Binding)
        setEqualsButtonLongClickListener(page1Binding)
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
            model.clearInput()
        } else if (v.id == R.id.buttonDel) {
            model.deleteLastCharacter()
        }
    }

    private fun onBinaryOperationSignButtonClick(v: Button) {
        val sign = when (v.id) {
            R.id.buttonRDiv -> "/"
            R.id.buttonMod -> ":"
            else -> v.text
        }

        model.appendBinaryOperationSign(sign)
    }

}