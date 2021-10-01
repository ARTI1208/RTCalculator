package ru.art2000.calculator.utils

import ru.art2000.calculator.model.calculator.numbers.DoubleField
import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.ExpressionValue
import ru.art2000.calculator.model.calculator.parts.Operation
import java.text.DecimalFormat
import java.text.NumberFormat

fun Double.expr(): ExpressionValue<Double> = ExpressionValue(this)

fun Int.expr(): ExpressionValue<Double> = ExpressionValue(this.toDouble())

inline fun <reified O : Operation<Double>> findOperation(textRepresentation: String): O {
    return DoubleField.operations.first { it is O && it.textRepresentations.contains(textRepresentation) } as O
}

fun findConstant(textRepresentation: String): Constant<Double> {
    return DoubleField.constants.first { it.symbols.contains(textRepresentation) }
}

fun Double.toDisplayFormat(): String {
    val nf: NumberFormat = DecimalFormat("#.#######")
    return nf.format(this)
}

fun Int.toDisplayFormat(): String {
    val nf: NumberFormat = DecimalFormat("#.#######")
    return nf.format(this.toLong())
}