package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.BaseCalculatorFormatter
import ru.art2000.calculator.calculator.computation.numbers.CalculationNumber
import java.text.DecimalFormat

internal object CalculatorFormatter : BaseCalculatorFormatter() {

    private val actualFormatter by lazy {
        DecimalFormat().apply {
            maximumFractionDigits = this@CalculatorFormatter.maximumFractionDigits
        }
    }

    override fun format(number: CalculationNumber<Double>): String {
        return actualFormatter.format(number.value)
    }
}