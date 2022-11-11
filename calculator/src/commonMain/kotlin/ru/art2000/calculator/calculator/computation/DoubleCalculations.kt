package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.numbers.DoubleField
import ru.art2000.calculator.calculator.computation.numbers.NumberField

class DoubleCalculations(
    override val formatter: CalculationNumberFormatter<Double>,
) : LexerParserCalculations<Double>() {

    override val field: NumberField<Double> = DoubleField

    override val parser = CalculationParser(DoubleParserConfiguration(field))

    override val lexer = CalculationLexer(DoubleLexerConfiguration(field))

    override fun calculate(expression: String, angleType: AngleType): Double? {
        return parser.parse(expression, angleType, lexer).compute()
    }

}