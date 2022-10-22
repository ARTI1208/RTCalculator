package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.ParserConfiguration
import ru.art2000.calculator.model.calculator.numbers.NumberField

class DoubleParserConfiguration(
    override val field: NumberField<Double>,
) : ParserConfiguration<Double> {

    override fun angleToRadiansConverter(originalNumber: Double, angleType: AngleType): Double {
        return when (angleType) {
            AngleType.DEGREES -> Math.toRadians(originalNumber)
            AngleType.RADIANS -> originalNumber
        }
    }

}