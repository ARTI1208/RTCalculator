package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.CalculationNumber
import java.text.DecimalFormat

internal object CalculatorIntFormatter : CalculationNumberFormatter<Int> {

    private val actualFormatter = DecimalFormat("#.#######")

    override fun format(number: CalculationNumber<Int>): String {
        return actualFormatter.format(number.value)
    }
}