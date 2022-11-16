package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.numbers.NumberField
import kotlin.math.PI

internal class DoubleParserConfiguration(
    override val field: NumberField<Double>,
) : ParserConfiguration<Double> {

    override fun angleToRadiansConverter(originalNumber: Double, angleType: AngleType): Double {
        return when (angleType) {
            AngleType.DEGREES -> originalNumber * PI / 180
            AngleType.RADIANS -> originalNumber
        }
    }

}