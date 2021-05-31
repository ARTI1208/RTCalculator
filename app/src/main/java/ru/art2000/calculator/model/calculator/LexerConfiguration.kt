package ru.art2000.calculator.model.calculator

import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.Operation
import ru.art2000.calculator.model.calculator.parts.PrefixOperation

interface LexerConfiguration<CalculationNumber> {

    val operations: List<Operation<CalculationNumber>>

    val constants: List<Constant<CalculationNumber>>

    val negateOperation: PrefixOperation<CalculationNumber>

    val identityOperation: PrefixOperation<CalculationNumber>

    fun numberConverter(numberString: String): CalculationNumber?

}