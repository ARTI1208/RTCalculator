package ru.art2000.calculator.calculator.computation.tests

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import ru.art2000.calculator.calculator.computation.utils.calculations
import ru.art2000.calculator.calculator.model.AngleType

@Suppress("unused")
class CorrectnessTest : FunSpec() {

    init {
        context(this::class.simpleName!!) {
            withData(nameFn = TestCase<*>::toString, getCases()) { testCase ->
                test(testCase.expressions, testCase.expectedResult, testCase.angleType)
            }
        }
    }

    companion object {

        private fun test(
            expressions: List<String>,
            expectedAnswer: String,
            angleType: AngleType = AngleType.RADIANS
        ) {
            expressions.forEach {
                test(it, expectedAnswer, angleType)
            }
        }

        private fun test(
            expression: String,
            expectedAnswer: String,
            angleType: AngleType = AngleType.RADIANS
        ) {
            val newResult = calculations.calculateForDisplay(expression, angleType)

            newResult shouldBe expectedAnswer
        }
    }
}