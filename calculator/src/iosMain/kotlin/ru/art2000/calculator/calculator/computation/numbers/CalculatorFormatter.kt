@file:Suppress("unused")

package ru.art2000.calculator.calculator.computation.numbers

import ru.art2000.calculator.calculator.computation.CalculationNumberFormatter

object CalculatorFormatter : CalculationNumberFormatter<Double> {

    override fun format(number: CalculationNumber<Double>): String {
        return number.value.toString()
    }
}