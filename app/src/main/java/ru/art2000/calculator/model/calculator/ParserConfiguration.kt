package ru.art2000.calculator.model.calculator

import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.Operation

interface ParserConfiguration<CalculationNumber> {

    val operations: List<Operation<CalculationNumber>>

    val constants: List<Constant<CalculationNumber>>

    fun angleToRadiansConverter(originalNumber: CalculationNumber, angleType: AngleType): CalculationNumber

}