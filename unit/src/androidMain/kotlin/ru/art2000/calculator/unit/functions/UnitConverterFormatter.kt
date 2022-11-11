package ru.art2000.calculator.unit.functions

import ru.art2000.calculator.calculator.computation.numbers.CalculationNumber
import ru.art2000.calculator.calculator.computation.CalculationNumberFormatter
import java.text.DecimalFormat
import kotlin.math.absoluteValue

internal object UnitConverterFormatter : CalculationNumberFormatter<Double> {

    private val actualFormatter
        get() = DecimalFormat("#.#######")

    private val scientificFormatter
        get() = DecimalFormat("#.#####E0")

    override fun format(number: CalculationNumber<Double>): String {

        val doubleValue = number.value
        val doubleValueAbsolute = doubleValue.absoluteValue

        if (doubleValueAbsolute <= 10e-5 || doubleValueAbsolute >= 10e5) return scientificFormatter.format(doubleValue)

        return actualFormatter.format(doubleValue)
    }
}