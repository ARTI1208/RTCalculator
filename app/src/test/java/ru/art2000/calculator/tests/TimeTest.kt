package ru.art2000.calculator.tests

import org.apache.commons.math3.special.Gamma
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.utils.OldCalculationClass
import ru.art2000.calculator.utils.OldLexerDoubleCalculations
import ru.art2000.calculator.utils.calculations
import ru.art2000.calculator.view_model.calculator.DoubleCalculations
import kotlin.system.measureNanoTime

@RunWith(Parameterized::class)
class TimeTest {

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testCases(): Iterable<TestCase<Double>> = TestCases.cases

        private fun test(expressions: List<String>, angleType: AngleType = AngleType.RADIANS) {
            expressions.forEach {
                test(it, angleType)
                lexerTest(it, angleType)
            }
        }

        private const val oldTimeMultiplierThreshold: Long = 400_000

        private const val oldTimeMultiplier: Double = 1.6

        private const val oldTimeMaxDiffOverThreshold: Long = 100_000

        private fun lexerTest(expression: String, angleType: AngleType = AngleType.RADIANS) {

            val arr = expression.toCharArray()

            val newTime = measureNanoTime {
                calculations.lexer.getLexemes(arr)
            }

            val oldTime = measureNanoTime {
                OldLexerDoubleCalculations.lexer.getLexemes(arr)
            }

            val times = """ 
                Expression: $expression
                Old l time: $oldTime
                New l time: $newTime
            """.trimIndent()

            val oldTimeCorrected = if (oldTime < oldTimeMultiplierThreshold)
                (oldTimeMultiplier * oldTime).toLong()
            else
                oldTime + oldTimeMaxDiffOverThreshold

            assert(newTime <= oldTimeCorrected) {
                times
            }
        }

        private fun test(expression: String, angleType: AngleType = AngleType.RADIANS) {

            val newResult: String
            val newTime = measureNanoTime {
                newResult = calculations.calculateForDisplay(expression, angleType)
            }

            var oldResult = ""
            val oldTime = try {
                OldCalculationClass.radians = angleType == AngleType.RADIANS
                val oldTime = measureNanoTime {
                    oldResult = OldCalculationClass.calculateStr(expression)
                }

                oldTime
            } catch (_: Exception) {
                -1
            }

            println("OldRes: $oldResult\nNew res: $newResult")

            val oldImplementationError = oldResult == "error" || oldResult != newResult

            val times = """ 
                Expression: $expression
                Old time: $oldTime
                New time: $newTime
            """.trimIndent()

            println("$times\nOld Implementation Error: $oldImplementationError")

            if (oldImplementationError) return

            if (oldTime < 0) return

            val oldTimeCorrected = if (oldTime < oldTimeMultiplierThreshold)
                (oldTimeMultiplier * oldTime).toLong()
            else
                oldTime + oldTimeMaxDiffOverThreshold

            assert(newTime <= oldTimeCorrected) {
                times
            }
        }
    }

    @Parameterized.Parameter
    lateinit var testCase: TestCase<Double>

    @Suppress("UNUSED_VARIABLE")
    @Before
    fun beforeEach() {
        val warm = calculations.calculateForDisplay("1+1")
        val warm2 = OldCalculationClass.calculateStr("1+1")
        val warm3 = Gamma.gamma(4.0)
        val warm4 = OldLexerDoubleCalculations.calculateForDisplay("1+1")
    }

    @Test
    fun runTestCases() {
        test(testCase.expressions, testCase.angleType)
    }
}