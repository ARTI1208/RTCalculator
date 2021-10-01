package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.numbers.CalculationNumber

interface CalculationNumberFormatter<T> {

    fun format(number: CalculationNumber<T>): String

}