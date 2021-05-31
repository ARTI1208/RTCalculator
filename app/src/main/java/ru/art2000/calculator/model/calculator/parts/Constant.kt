package ru.art2000.calculator.model.calculator.parts

data class Constant<O>(val symbols: List<String>, val value: O) : ExpressionPart<O> {

    init {
        require(symbols.isNotEmpty()) {
            "Operation should specify at least one text representation"
        }
    }

    constructor(symbol: String, value: O) : this(listOf(symbol), value)

    override fun partAsString(): String = symbols.first()
}
