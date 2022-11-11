package ru.art2000.calculator.calculator.computation.tests

import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.parts.ExpressionPart

internal data class TestCase<CalculationNumber>(
    val expressions: List<String>,
    val expectedLexemes: List<ExpressionPart<CalculationNumber>>,
    val expectedResult: String,
    val angleType: AngleType = AngleType.RADIANS
) {

    override fun toString(): String {
        return expressions.joinToString()
    }
}