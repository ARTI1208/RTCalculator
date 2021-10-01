package ru.art2000.calculator.model.calculator

import ru.art2000.calculator.model.calculator.numbers.NumberField

interface ParserConfiguration<CalculationNumber> {

    val field: NumberField<CalculationNumber>

    fun angleToRadiansConverter(originalNumber: CalculationNumber, angleType: AngleType): CalculationNumber

}