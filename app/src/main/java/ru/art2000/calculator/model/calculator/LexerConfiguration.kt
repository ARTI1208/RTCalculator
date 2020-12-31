package ru.art2000.calculator.model.calculator

interface LexerConfiguration<CalculationNumber> {

    val operations: List<Operation<CalculationNumber>>

    val constants: List<Constant<CalculationNumber>>

    val negateOperation: PrefixOperation<CalculationNumber>

    val identityOperation: PrefixOperation<CalculationNumber>

    fun numberConverter(numberString: String): CalculationNumber?

}