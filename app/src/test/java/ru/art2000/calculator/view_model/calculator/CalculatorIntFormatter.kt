package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.numbers.CalculationNumber
import java.text.DecimalFormat

object CalculatorIntFormatter : CalculationNumberFormatter<Int> {

    private val actualFormatter = DecimalFormat("#.#######")

    override fun format(number: CalculationNumber<Int>): String {
        return actualFormatter.format(number.value)
    }
}