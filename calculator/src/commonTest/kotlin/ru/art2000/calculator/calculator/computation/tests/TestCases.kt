@file:Suppress("unused")

package ru.art2000.calculator.calculator.computation.tests

import ru.art2000.calculator.calculator.computation.Calculations
import ru.art2000.calculator.calculator.computation.parts.*
import ru.art2000.calculator.calculator.computation.utils.expr
import ru.art2000.calculator.calculator.computation.utils.findConstant
import ru.art2000.calculator.calculator.computation.utils.findOperation
import ru.art2000.calculator.calculator.computation.utils.toDisplayFormat
import ru.art2000.calculator.calculator.model.AngleType

internal expect fun getCases(): List<TestCase<Double>>

internal object TestCases {

    private val binaryPlus = findOperation<BinaryOperation<Double>>("+")
    private val unaryPlus = findOperation<UnaryOperation<Double>>("+")
    private val unaryMinus = findOperation<UnaryOperation<Double>>("-")
    private val cos = findOperation<UnaryOperation<Double>>("cos")
    private val integerDiv = findOperation<BinaryOperation<Double>>("/")
    private val div = findOperation<BinaryOperation<Double>>("÷")
    private val mod = findOperation<BinaryOperation<Double>>(":")
    private val times = findOperation<BinaryOperation<Double>>("*")
    private val binaryMinus = findOperation<BinaryOperation<Double>>("-")
    private val sqrt = findOperation<UnaryOperation<Double>>("sqrt")
    private val factorial = findOperation<UnaryOperation<Double>>("!")

    private val pi = findConstant("pi")
    private val euler = findConstant("e")

    private val openingBracket = BlockOpenExpression<Double>()
    private val closingBracket = BlockCloseExpression<Double>()

    private fun testPrefixOrBinary(): TestCase<Double> {
        val expressions = listOf(
            "- 2.5 + -4",
            "- 2.5 + - 4",
        )

        val expectedLexemes = listOf(
            (-2.5).expr(), binaryPlus, (-4).expr()
        )

        val answer = -6.5

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testBinaryAndManyPrefix(): TestCase<Double> {
        val expressions = listOf(
            "- 2.5 +------ -4",
            "- 2.5 +------ - 4",
        )

        val expectedLexemes = listOf(
            (-2.5).expr(),
            binaryPlus,
            unaryMinus, unaryMinus, unaryMinus, unaryMinus, unaryMinus, unaryMinus, // 6 times
            (-4).expr(),
        )

        val answer = -6.5

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testCosBracketsPi(): TestCase<Double> {
        val expressions = listOf(
            "cos(π)",
            "cos(pi)",
        )

        val expectedLexemes = listOf(
            cos, openingBracket, pi, closingBracket,
        )

        val answer = -1

        return TestCase(expressions, expectedLexemes, answer, AngleType.RADIANS)
    }

    fun testCosNoBracketsPi(): TestCase<Double> {
        val expressions = listOf(
            "cosπ",
            "cospi",
        )

        val expectedLexemes = listOf(
            cos, pi,
        )

        val answer = -1

        return TestCase(expressions, expectedLexemes, answer, AngleType.RADIANS)
    }

    fun testCosBrackets90(): TestCase<Double> {
        val expressions = listOf(
            "cos(90)",
        )

        val expectedLexemes = listOf(
            cos, openingBracket, 90.expr(), closingBracket,
        )

        val answer = 0

        return TestCase(expressions, expectedLexemes, answer, AngleType.DEGREES)
    }

    fun testCosNoBrackets90(): TestCase<Double> {
        val expressions = listOf(
            "cos90",
        )

        val expectedLexemes = listOf(
            cos, 90.expr(),
        )

        val answer = 0

        return TestCase(expressions, expectedLexemes, answer, AngleType.DEGREES)
    }

    fun testLong(): TestCase<Double> {
        val expressions = listOf(
            "1 +1 ++1 +++1 ++++ 1",
        )

        val oneExpr = 1.expr()

        val expectedLexemes = listOf(
            oneExpr,
            binaryPlus,
            oneExpr,
            binaryPlus,
            oneExpr,
            binaryPlus,
            unaryPlus,
            oneExpr,
            binaryPlus,
            unaryPlus, unaryPlus,
            oneExpr,
        )

        val answer = 5

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testComplicated(): TestCase<Double> {
        val expressions = listOf(
            "cos(90)+(43/42)-((√144):(3!))",
        )

        val e90 = 90.expr()
        val e43 = 43.expr()
        val e42 = 42.expr()
        val e144 = 144.expr()
        val e3 = 3.expr()

        val expectedLexemes = listOf(
            cos, openingBracket, e90, closingBracket,
            binaryPlus,
            openingBracket, e43, integerDiv, e42, closingBracket,
            binaryMinus,
            openingBracket,
            openingBracket, sqrt, e144, closingBracket,
            mod,
            openingBracket, e3, factorial, closingBracket,
            closingBracket,
        )

        val answer = 1

        return TestCase(expressions, expectedLexemes, answer, AngleType.DEGREES)
    }

    fun constantTest(): TestCase<Double> {

        val expressions = listOf(
            "e",
            "   e",
            "e    ",
            "     e    ",
        )

        val answer = euler.value

        return TestCase(expressions, listOf(euler), answer)
    }

    fun numberTest(): TestCase<Double> {

        val expressions = listOf(
            "6",
            "   6",
            "6    ",
            "     6    ",
        )

        val answer = 6

        return TestCase(expressions, listOf(6.expr()), answer)
    }

    fun simpleTest(): TestCase<Double> {

        val expressions = listOf(
            "6+8",
            "   6+8",
            "6+8    ",
            "     6+ 8    ",
            "     6  +8    ",
            "     6 + 8    ",
        )

        val expectedLexemes = listOf(6.expr(), binaryPlus, 8.expr())

        val answer = 14

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testLongRepetitive(): TestCase<Double> {
        val expression = "1+1+1+1+1+1+1+1+1+1+1+1+1" // 13 of '1'

        val e1 = 1.expr()

        val expectedLexemes = MutableList(13) { e1 }
            .zipWithNext { a, _ -> listOf(a, binaryPlus) }
            .flatten() + e1

        val answer = 13

        return TestCase(expression, expectedLexemes, answer)
    }

    fun testFloatingPoint(): TestCase<Double> {
        val expressions = listOf(
            "-2.5+4", // dot
            "-2,5+4", // comma
        )

        val expectedLexemes = listOf(
            (-2.5).expr(), binaryPlus, 4.expr()
        )

        val answer = 1.5

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testMinus(): TestCase<Double> {
        val expressions = listOf(
            "-(8-10)"
        )

        val expectedLexemes = listOf(
            unaryMinus, openingBracket, 8.expr(), binaryMinus, 10.expr(), closingBracket
        )

        val answer = 2

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testIntegerDivFloatingNegative(): TestCase<Double> {
        val expressions = listOf(
            "-6.4/2"
        )

        val expectedLexemes = listOf(
            (-6.4).expr(), integerDiv, 2.expr()
        )

        val answer = -3

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testIntegerDivIntegerNegative(): TestCase<Double> {
        val expressions = listOf(
            "-6/2"
        )

        val expectedLexemes = listOf(
            (-6).expr(), integerDiv, 2.expr()
        )

        val answer = -3

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testStrangeInput(): TestCase<Double> {
        val expressions = listOf(
            "binding.valueOriginal"
        )

        val expectedLexemes = emptyList<ExpressionPart<Double>>()

        val answer = Calculations.CALCULATION_ERROR

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testUnaryMinus(): TestCase<Double> {
        val expressions = listOf(
            "1÷(1÷30+1÷40)-2×(4-3)"
        )

        val expectedLexemes = listOf(
            1.expr(), div,
            openingBracket, 1.expr(), div, 30.expr(), binaryPlus, 1.expr(), div, 40.expr(),
            closingBracket,
            binaryMinus, 2.expr(), times,
            openingBracket, 4.expr(), binaryMinus, 3.expr(), closingBracket
        )

        val answer = (1.0 / ((1.0 / 30) + (1.0 / 40))) - 2 * (4 - 3)

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testUnaryMinusInBrackets(): TestCase<Double> {
        val expressions = listOf(
            "6×(-8)"
        )

        val expectedLexemes = listOf(
            6.expr(), times, openingBracket, (-8).expr(), closingBracket
        )

        val answer = 6 * -8

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testScientificNotation(): TestCase<Double> {
        val expressions = listOf(
            "2×1.1e2"
        )

        val expectedLexemes = listOf(
            2.expr(), times, 1.1e2.expr()
        )

        val answer = 2 * 1.1e2

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testLongExpression(): TestCase<Double> {
        val expressions = listOf(
            "6 666 466 131×8 115 214"
        )

        val expectedLexemes = listOf(
            6666466131.expr(), times, 8115214.expr()
        )

        val answer = 6666466131 * 8115214

        return TestCase(expressions, expectedLexemes, answer)
    }

    fun testExponentNumber(): TestCase<Double> {
        val expressions = listOf(
            "1e-7",
            "1E-7",
        )

        val expectedLexemes = listOf(
            1e-7.expr()
        )

        val answer = 1e-7

        return TestCase(expressions, expectedLexemes, answer)
    }

    @Suppress("TestFunctionName")
    private fun TestCase(
        expressions: List<String>,
        expectedLexemes: List<ExpressionPart<Double>>,
        expectedResult: Number,
        angleType: AngleType = AngleType.RADIANS
    ): TestCase<Double> = TestCase(
        expressions,
        expectedLexemes,
        expectedResult.toDouble().toDisplayFormat(),
        angleType
    )

    @Suppress("TestFunctionName")
    private fun TestCase(
        expression: String,
        expectedLexemes: List<ExpressionPart<Double>>,
        expectedResult: Number,
        angleType: AngleType = AngleType.RADIANS
    ): TestCase<Double> = TestCase(listOf(expression), expectedLexemes, expectedResult, angleType)

}