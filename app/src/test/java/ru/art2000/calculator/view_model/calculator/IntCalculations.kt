package ru.art2000.calculator.view_model.calculator

import androidx.annotation.VisibleForTesting
import ru.art2000.calculator.model.calculator.AngleType
import ru.art2000.calculator.model.calculator.LexerConfiguration
import ru.art2000.calculator.model.calculator.ParserConfiguration
import ru.art2000.calculator.model.calculator.numbers.CalculationNumber
import ru.art2000.calculator.model.calculator.numbers.NumberField
import ru.art2000.calculator.model.calculator.parts.BinaryOperation
import ru.art2000.calculator.model.calculator.parts.Constant
import ru.art2000.calculator.model.calculator.parts.Operation
import ru.art2000.calculator.model.calculator.parts.PrefixOperation

class IntCalculations(
    override val formatter: CalculationNumberFormatter<Int>,
) : LexerParserCalculations<Int>() {

    override val field = object : NumberField<Int> {

        override val negateOperation = PrefixOperation<Int>("-", { -it })

        override val identityOperation = PrefixOperation<Int>("+", { it })

        override val operations: List<Operation<Int>> = listOf(
            negateOperation,
            identityOperation,
            BinaryOperation("+", { a, b -> a + b }, 0),
            BinaryOperation("-", { a, b -> a - b }, 0),
        )

        override val constants: List<Constant<Int>>
            get() = emptyList()

        override fun toCalculationNumber(value: Int): CalculationNumber<Int> {
            val f = this
            return object : CalculationNumber<Int> {

                override val field: NumberField<Int>
                    get() = f

                override val value = value

                override val isInfinite = false

            }
        }

        override fun isNumberPart(c: Char): Boolean {
            return c in '0'..'9'
        }

        override fun isZeroOrClose(fieldNumber: String): Boolean {
            return fieldNumber.toInt() == 0
        }

    }

    private val config = object : ParserConfiguration<Int>, LexerConfiguration<Int> {

        override val field = this@IntCalculations.field

        override fun numberConverter(numberString: String): Int? {
            return numberString.toIntOrNull()
        }

        override fun angleToRadiansConverter(originalNumber: Int, angleType: AngleType): Int {
            return when (angleType) {
                AngleType.DEGREES -> Math.toRadians(originalNumber.toDouble()).toInt()
                AngleType.RADIANS -> originalNumber
            }
        }

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override val parser = CalculationParser(config)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    override val lexer = CalculationLexer(config)

    override fun calculate(expression: String, angleType: AngleType): Int? {
        return parser.parse(expression, angleType, lexer).compute()
    }

}