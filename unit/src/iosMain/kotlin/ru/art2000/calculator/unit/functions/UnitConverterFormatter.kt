package ru.art2000.calculator.unit.functions

import ru.art2000.calculator.calculator.computation.numbers.CalculationNumber
import ru.art2000.calculator.calculator.computation.CalculationNumberFormatter

internal object UnitConverterFormatter : CalculationNumberFormatter<Double> {

    override fun format(number: CalculationNumber<Double>): String {

        return number.value.toString()
    }
}