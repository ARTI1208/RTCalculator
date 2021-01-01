package ru.art2000.calculator.model.calculator

data class Constant<O>(val symbols: List<String>, val value: O) : ExpressionPart<O> {

    constructor(symbol: String, value: O) : this(listOf(symbol), value)
}
