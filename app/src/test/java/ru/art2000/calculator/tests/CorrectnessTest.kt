package ru.art2000.calculator.tests

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.utils.OldCalculationClass
import ru.art2000.calculator.view_model.calculator.DoubleCalculations

@RunWith(Parameterized::class)
class CorrectnessTest {

    companion object {

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun testCases(): Iterable<TestCase<Double>> = TestCases.cases

        private fun test(expressions: List<String>, expectedAnswer: String, angleType: AngleType = AngleType.RADIANS) {
            expressions.forEach {
                test(it, expectedAnswer, angleType)
            }
        }

        private fun test(expression: String, expectedAnswer: String, angleType: AngleType = AngleType.RADIANS) {
            val newResult: String = DoubleCalculations.calculateForDisplay(expression, angleType)

            val oldResult = try {
                OldCalculationClass.radians = angleType == AngleType.RADIANS
                OldCalculationClass.calculateStr(expression)
            } catch (_: Exception) {
                ""
            }

            assert(newResult == expectedAnswer) {
                """ 
                Expression: $expression
                Expected result: $expectedAnswer
                New result:      $newResult
            """.trimIndent()
            }

            if (newResult != oldResult) {
                val oldNotEquals = """ 
                Expression: $expression
                Old result: $oldResult
                New result: $newResult
            """.trimIndent()

                println(oldNotEquals)
            }
        }
    }

    @Parameterized.Parameter
    lateinit var testCase: TestCase<Double>

    @Test
    fun runTestCases() {
        test(testCase.expressions, testCase.expectedResult, testCase.angleType)
    }
}