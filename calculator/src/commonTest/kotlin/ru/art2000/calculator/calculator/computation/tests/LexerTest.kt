package ru.art2000.calculator.calculator.computation.tests

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactly
import ru.art2000.calculator.calculator.computation.parts.ExpressionPart
import ru.art2000.calculator.calculator.computation.utils.calculations

@Suppress("unused")
class LexerTest : FunSpec() {

    init {
        context(this::class.simpleName!!) {
            withData(nameFn = TestCase<*>::toString, getCases()) { testCase ->
                test(testCase.expressions, testCase.expectedLexemes)
            }
        }
    }

    companion object {

        private inline val lexer get() = calculations.lexer

        private fun test(
            expressions: List<String>,
            expectedLexemes: List<ExpressionPart<Double>>
        ) {
            expressions.forEach {
                test(it, expectedLexemes)
            }
        }

        private fun test(expression: String, expectedLexemes: List<ExpressionPart<Double>>) {
            println(expression)
            val actualLexemes = lexer.getLexemes(expression.toCharArray()) ?: emptyList()

            actualLexemes.shouldContainExactly(expectedLexemes)
        }
    }
}