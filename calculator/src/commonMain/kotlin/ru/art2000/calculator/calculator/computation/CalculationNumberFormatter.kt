package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.CalculationNumber

interface CalculationNumberFormatter<T> {

    fun format(number: CalculationNumber<T>): String

}