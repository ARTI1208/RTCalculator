package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.numbers.DoubleMath
import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.numbers.NumberField

internal class DoubleParserConfiguration(
    override val field: NumberField<Double>,
) : ParserConfiguration<Double> {

    override fun angleToRadiansConverter(originalNumber: Double, angleType: AngleType): Double {
        return when (angleType) {
            AngleType.DEGREES -> DoubleMath.toRadians(originalNumber)
            AngleType.RADIANS -> originalNumber
        }
    }

}