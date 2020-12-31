package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.Constant
import ru.art2000.calculator.model.calculator.Operation
import ru.art2000.calculator.model.calculator.ParserConfiguration
import ru.art2000.extensions.safeToDouble

class DoubleParserConfiguration(
        override val operations: List<Operation<Double>>,
        override val constants: List<Constant<Double>>
) : ParserConfiguration<Double> {

    override fun angleToRadiansConverter(originalNumber: Double, angleType: AngleType): Double {
        return when (angleType) {
            AngleType.DEGREES -> originalNumber * Math.PI / 180
            AngleType.RADIANS -> originalNumber
        }
    }

}