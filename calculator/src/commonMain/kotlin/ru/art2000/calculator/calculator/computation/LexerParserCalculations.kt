package ru.art2000.calculator.calculator.computation

abstract class LexerParserCalculations<T> : Calculations<T>() {

    internal abstract val parser: CalculationParser<T>

    internal abstract val lexer: CalculationLexer<T>
}