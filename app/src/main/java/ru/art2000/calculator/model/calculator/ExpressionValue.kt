package ru.art2000.calculator.model.calculator

import ru.art2000.calculator.view_model.calculator.CalculationClass

data class ExpressionValue<O>(val value: O) : ExpressionPart<O> {

    override fun toString() = value.toString()

    override fun partAsString(): String = if (value is Number)
        CalculationClass.numberFormatter.format(value)
    else
        value.toString()
}