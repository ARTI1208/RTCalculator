package ru.art2000.calculator.view_model.unit

import ru.art2000.calculator.model.calculator.numbers.CalculationNumber
import ru.art2000.calculator.view_model.calculator.CalculationNumberFormatter
import java.text.DecimalFormat

object UnitConverterFormatter : CalculationNumberFormatter<Double> {

    private val actualFormatter = DecimalFormat("#.#######")
    private val scientificFormatter = DecimalFormat("#.#####E0")

    override fun format(number: CalculationNumber<Double>): String {

        val doubleValue = number.value

        if ((doubleValue <= 10e-5 || doubleValue >= 10e5)) return scientificFormatter.format(doubleValue)

        return actualFormatter.format(doubleValue)
    }
}