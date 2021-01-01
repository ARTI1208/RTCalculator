package ru.art2000.calculator.utils

import ru.art2000.calculator.model.calculator.Constant
import ru.art2000.calculator.model.calculator.ExpressionValue
import ru.art2000.calculator.model.calculator.Operation
import ru.art2000.calculator.view_model.calculator.CalculationClass
import java.text.DecimalFormat
import java.text.NumberFormat

fun Double.expr(): ExpressionValue<Double> = ExpressionValue(this)

fun Int.expr(): ExpressionValue<Double> = ExpressionValue(this.toDouble())

inline fun <reified O : Operation<Double>> findOperation(textRepresentation: String): O {
    return CalculationClass.operations.first { it is O && it.textRepresentations.contains(textRepresentation) } as O
}

fun findConstant(textRepresentation: String): Constant<Double> {
    return CalculationClass.constants.first { it.symbols.contains(textRepresentation) }
}

fun Double.toDisplayFormat(): String {
    val nf: NumberFormat = DecimalFormat("#.#######")
    return nf.format(this)
}

fun Int.toDisplayFormat(): String {
    val nf: NumberFormat = DecimalFormat("#.#######")
    return nf.format(this.toLong())
}