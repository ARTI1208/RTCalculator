package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.NumberField
import ru.art2000.extensions.strings.safeToDouble

internal class DoubleLexerConfiguration(
    override val field: NumberField<Double>,
) : LexerConfiguration<Double> {

    override fun numberConverter(numberString: String): Double? = numberString.safeToDouble()

}