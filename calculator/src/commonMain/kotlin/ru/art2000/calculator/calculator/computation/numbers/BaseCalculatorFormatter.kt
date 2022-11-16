package ru.art2000.calculator.calculator.computation.numbers

import ru.art2000.calculator.calculator.computation.CalculationNumberFormatter

internal abstract class BaseCalculatorFormatter : CalculationNumberFormatter<Double> {

    protected val maximumFractionDigits = 7

}