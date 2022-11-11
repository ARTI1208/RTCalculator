package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.CalculationNumber
import java.text.DecimalFormat

internal object CalculatorFormatter : CalculationNumberFormatter<Double> {

    private val actualFormatter
        get() = DecimalFormat("#.#######")

    override fun format(number: CalculationNumber<Double>): String {
        return actualFormatter.format(number.value)
    }
}