package ru.art2000.calculator.model.calculator

interface ParserConfiguration<CalculationNumber> {

    val operations: List<Operation<CalculationNumber>>

    val constants: List<Constant<CalculationNumber>>

    fun angleToRadiansConverter(originalNumber: CalculationNumber, angleType: AngleType): CalculationNumber

}