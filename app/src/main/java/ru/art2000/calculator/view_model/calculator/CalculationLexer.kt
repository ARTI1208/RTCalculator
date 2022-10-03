package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.parts.*

interface CalculationLexer<CalculationNumber> {

    fun getLexemes(expression: CharArray): List<ExpressionPart<CalculationNumber>>?

}