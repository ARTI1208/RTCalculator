package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.LexerConfiguration
import ru.art2000.calculator.model.calculator.numbers.NumberField
import ru.art2000.extensions.language.safeToDouble

class DoubleLexerConfiguration(
        override val field: NumberField<Double>,
) : LexerConfiguration<Double> {

    override fun numberConverter(numberString: String): Double? = numberString.safeToDouble()

}