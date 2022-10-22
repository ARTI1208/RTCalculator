package ru.art2000.calculator.view_model.calculator

import androidx.annotation.VisibleForTesting
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.numbers.DoubleField

class DoubleCalculations(
    override val formatter: CalculationNumberFormatter<Double>,
) : LexerParserCalculations<Double>() {

    override val field = DoubleField

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override val parser = CalculationParser(DoubleParserConfiguration(field))

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override val lexer = CalculationLexer(DoubleLexerConfiguration(field))

    override fun calculate(expression: String, angleType: AngleType): Double? {
        return parser.parse(expression, angleType, lexer).compute()
    }

}