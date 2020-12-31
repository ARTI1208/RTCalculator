package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.Constant
import ru.art2000.calculator.model.calculator.LexerConfiguration
import ru.art2000.calculator.model.calculator.Operation
import ru.art2000.calculator.model.calculator.PrefixOperation
import ru.art2000.extensions.safeToDouble

class DoubleLexerConfiguration(
        override val operations: List<Operation<Double>>,
        override val constants: List<Constant<Double>>
) : LexerConfiguration<Double> {

    override val negateOperation: PrefixOperation<Double> =
            operations.first {
                it is PrefixOperation && it.textRepresentations.contains("-")
            } as PrefixOperation<Double>

    override val identityOperation: PrefixOperation<Double> =
            operations.first {
                it is PrefixOperation && it.textRepresentations.contains("+")
            } as PrefixOperation<Double>

    override fun numberConverter(numberString: String): Double? = numberString.safeToDouble()

}