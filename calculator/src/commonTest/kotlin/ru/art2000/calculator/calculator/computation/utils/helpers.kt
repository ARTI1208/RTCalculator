package ru.art2000.calculator.calculator.computation.utils

import ru.art2000.calculator.calculator.computation.numbers.DoubleField
import ru.art2000.calculator.calculator.computation.parts.Constant
import ru.art2000.calculator.calculator.computation.parts.ExpressionValue
import ru.art2000.calculator.calculator.computation.parts.Operation
import ru.art2000.calculator.calculator.computation.DoubleCalculations

internal fun Number.expr(): ExpressionValue<Double> = ExpressionValue(this.toDouble())

internal inline fun <reified O : Operation<Double>> findOperation(textRepresentation: String): O {
    return DoubleField.operations.first { it is O && it.textRepresentations.contains(textRepresentation) } as O
}

internal fun findConstant(textRepresentation: String): Constant<Double> {
    return DoubleField.constants.first { it.symbols.contains(textRepresentation) }
}

internal expect val calculations: DoubleCalculations

internal fun Double.toDisplayFormat(): String {
    return calculations.format(this)
}