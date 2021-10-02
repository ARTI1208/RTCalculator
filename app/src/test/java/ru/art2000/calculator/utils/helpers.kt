package ru.art2000.calculator.utils

import ru.art2000.calculator.model.calculator.numbers.DoubleField
import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.ExpressionValue
import ru.art2000.calculator.model.calculator.parts.Operation
import ru.art2000.calculator.view_model.calculator.CalculatorFormatter
import ru.art2000.calculator.view_model.calculator.DoubleCalculations

fun Number.expr(): ExpressionValue<Double> = ExpressionValue(this.toDouble())

inline fun <reified O : Operation<Double>> findOperation(textRepresentation: String): O {
    return DoubleField.operations.first { it is O && it.textRepresentations.contains(textRepresentation) } as O
}

fun findConstant(textRepresentation: String): Constant<Double> {
    return DoubleField.constants.first { it.symbols.contains(textRepresentation) }
}

val calculations = DoubleCalculations(CalculatorFormatter)

fun Double.toDisplayFormat(): String {
    return calculations.format(this)
}