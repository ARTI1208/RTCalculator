package ru.art2000.calculator.model.calculator

import ru.art2000.calculator.model.calculator.numbers.NumberField

interface LexerConfiguration<CalculationNumber> {

    val field: NumberField<CalculationNumber>

    fun numberConverter(numberText: CharSequence): CalculationNumber?

}