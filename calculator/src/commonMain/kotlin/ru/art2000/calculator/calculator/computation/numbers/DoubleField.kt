package ru.art2000.calculator.calculator.computation.numbers

import ru.art2000.calculator.calculator.computation.parts.*
import ru.art2000.extensions.strings.dotSafeToDouble
import kotlin.math.*

internal object DoubleField : NumberField<Double> {

    private const val PRECISION = 1e-7

    override val constants: List<Constant<Double>>
        get() = listOf(
                Constant("e", E),
                Constant(listOf("π", "pi"), PI),
                Constant("φ", 1.6180339887),
        )

    override val operations: List<Operation<Double>>
        get() = listOf(
                // From ones with lower priority to ones with higher

                // Prefix operations
                PrefixOperation("-", DoubleField::negate),
                PrefixOperation("+", { it }),
                PrefixOperation(setOf("√", "sqrt"), DoubleField::squareRoot),
                PrefixOperation("lg", DoubleField::log10),
                PrefixOperation("ln", DoubleField::logNatural),
                PrefixOperation("cos", DoubleField::cosine, true),
                PrefixOperation("sin", DoubleField::sine, true),
                PrefixOperation("ctg", DoubleField::cotangent, true),
                PrefixOperation("tan", DoubleField::tangent, true),

                // Binary operations
                BinaryOperation("+", DoubleField::addition, 0),
                BinaryOperation("-", DoubleField::subtraction, 0),
                BinaryOperation(setOf("×", "*"), DoubleField::multiplication, 1),
                BinaryOperation("÷", DoubleField::division, 1),
                BinaryOperation(setOf("/", "div"), DoubleField::integerDivision, 1),
                BinaryOperation(setOf(":", "mod"), DoubleField::divisionRemainder, 1),
                BinaryOperation("^", DoubleField::power, 2),

                // Postfix operations
                PostfixOperation("%", DoubleField::percent),
                PostfixOperation("!", DoubleField::factorial),

                )

    private fun factorial(x: Double): Double = DoubleMath.factorial(x)

    private fun negate(x: Double): Double = -x

    private fun percent(x: Double): Double = x / 100

    // Binary operations

    private fun power(left: Double, right: Double): Double = left.pow(right)

    private fun multiplication(left: Double, right: Double): Double = left * right

    private fun divisionRemainder(left: Double, right: Double): Double {
        if (right.isZero) return Double.POSITIVE_INFINITY

        return left % right
    }

    private fun integerDivision(left: Double, right: Double): Double {
        if (right.isZero) return Double.POSITIVE_INFINITY

        val doubleResult = (left / right).absoluteValue
        return floor(doubleResult) * left.sign * right.sign
    }

    private fun division(left: Double, right: Double): Double {
        if (right.isZero) return Double.POSITIVE_INFINITY

        return left / right
    }

    private fun addition(left: Double, right: Double): Double = left + right

    private fun subtraction(left: Double, right: Double): Double = left - right

    // Prefix operations
    private fun squareRoot(operand: Double): Double? = if (operand < 0) null else sqrt(operand)

    private fun log10(operand: Double): Double? {
        if (operand < 0) return null

        return kotlin.math.log10(operand)
    }

    private fun logNatural(operand: Double): Double? {
        if (operand < 0) return null

        return ln(operand)
    }

    private fun cosine(operand: Double): Double = cos(operand)

    private fun sine(operand: Double): Double = sin(operand)

    private fun cotangent(operand: Double): Double = cos(operand) / sin(operand)

    private fun tangent(operand: Double): Double = tan(operand)

    private inline val Double.isZero: Boolean
        get() = abs(this) < PRECISION

    override val negateOperation: PrefixOperation<Double>
        get() = PrefixOperation("-", ::negate)

    override val identityOperation: PrefixOperation<Double>
        get() = PrefixOperation("+", { it })


    override fun toCalculationNumber(value: Double): CalculationNumber<Double> = CalculationDouble(value)

    override fun isNumberPart(c: Char): Boolean {
        return c in '0'..'9'
    }

    override fun isZeroOrClose(fieldNumber: String): Boolean {
        return fieldNumber.dotSafeToDouble() == 0.0
    }
}

internal expect object DoubleMath {

    fun factorial(x: Double): Double

    fun toRadians(x: Double): Double

}