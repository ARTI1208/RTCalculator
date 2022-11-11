package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.NumberField

internal interface LexerConfiguration<CalculationNumber> {

    val field: NumberField<CalculationNumber>

    fun numberConverter(numberString: String): CalculationNumber?

}