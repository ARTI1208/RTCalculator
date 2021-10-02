package ru.art2000.calculator.tests

import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.parts.ExpressionPart
import ru.art2000.calculator.view_model.calculator.CalculationNumberFormatter

data class TestCase<CalculationNumber>(
    val expressions: List<String>,
    val expectedLexemes: List<ExpressionPart<CalculationNumber>>?,
    val expectedResult: String,
    val angleType: AngleType = AngleType.RADIANS
) {

    constructor(
        expression: String,
        expectedLexemes: List<ExpressionPart<CalculationNumber>>,
        expectedResult: String,
        angleType: AngleType = AngleType.RADIANS
    ) : this(listOf(expression), expectedLexemes, expectedResult, angleType)

    override fun toString(): String {
        return expressions.joinToString()
    }
}