package ru.art2000.calculator.model.calculator.numbers

import org.apache.commons.math3.special.Gamma
import ru.art2000.calculator.model.calculator.parts.*
import kotlin.math.*

object DoubleField : NumberField<Double> {

    private const val PRECISION = 1e-7

    override val precision: Double
        get() = PRECISION

    override val constants: List<Constant<Double>>
        get() = listOf(
                Constant("e", Math.E),
                Constant(listOf("π", "pi"), Math.PI),
                Constant("φ", 1.6180339887),
        )

    override val operations: List<Operation<Double>>
        get() = listOf(
                // From ones with lower priority to ones with higher

                // Prefix operations
                PrefixOperation("-", ::negate),
                PrefixOperation("+", { it }),
                PrefixOperation(setOf("√", "sqrt"), ::squareRoot),
                PrefixOperation("lg", ::log10),
                PrefixOperation("ln", ::logNatural),
                PrefixOperation("cos", ::cosine, true),
                PrefixOperation("sin", ::sine, true),
                PrefixOperation("ctg", ::cotangent, true),
                PrefixOperation("tan", ::tangent, true),

                // Binary operations
                BinaryOperation("+", ::addition, 0),
                BinaryOperation("-", ::subtraction, 0),
                BinaryOperation(setOf("×", "*"), ::multiplication, 1),
                BinaryOperation("÷", ::division, 1),
                BinaryOperation(setOf("/", "div"), ::integerDivision, 1),
                BinaryOperation(setOf(":", "mod"), ::divisionRemainder, 1),
                BinaryOperation("^", ::power, 2),

                // Postfix operations
                PostfixOperation("%", ::percent),
                PostfixOperation("!", ::factorial),

                )

    private fun factorial(x: Double): Double = Gamma.gamma(x + 1)

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

    override fun isFloatingPointSymbol(c: Char): Boolean {
        return c == '.' || c == ','
    }
}