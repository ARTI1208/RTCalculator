package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.numbers.CalculationNumber
import java.text.DecimalFormat

object CalculatorFormatter : CalculationNumberFormatter<Double> {

    private val actualFormatter
        get() = DecimalFormat("#.#######")

    override fun format(number: CalculationNumber<Double>): String {
        return actualFormatter.format(number.value)
    }
}