package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.numbers.NumberField

internal interface ParserConfiguration<CalculationNumber> {

    val field: NumberField<CalculationNumber>

    fun angleToRadiansConverter(originalNumber: CalculationNumber, angleType: AngleType): CalculationNumber

}