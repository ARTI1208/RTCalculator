package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.computation.numbers.DoubleMath
import ru.art2000.calculator.calculator.computation.numbers.IntField
import ru.art2000.calculator.calculator.computation.numbers.NumberField

class IntCalculations(
    override val formatter: CalculationNumberFormatter<Int>,
) : LexerParserCalculations<Int>() {

    override val field: NumberField<Int> = IntField

    private val config = object : ParserConfiguration<Int>, LexerConfiguration<Int> {

        override val field = this@IntCalculations.field

        override fun numberConverter(numberString: String): Int? {
            return numberString.toIntOrNull()
        }

        override fun angleToRadiansConverter(originalNumber: Int, angleType: AngleType): Int {
            return when (angleType) {
                AngleType.DEGREES -> DoubleMath.toRadians(originalNumber.toDouble()).toInt()
                AngleType.RADIANS -> originalNumber
            }
        }

    }

    override val parser = CalculationParser(config)

    override val lexer = CalculationLexer(config)

    override fun calculate(expression: String, angleType: AngleType): Int? {
        return parser.parse(expression, angleType, lexer).compute()
    }

}