package ru.art2000.calculator.view_model.calculator

import android.content.Context
import org.apache.commons.math3.special.Gamma
import ru.art2000.calculator.R
import ru.art2000.calculator.model.calculator.*
import ru.art2000.helpers.PrefsHelper
import java.text.DecimalFormat
import kotlin.math.*

object CalculationClass {

    val operations: List<Operation<Double>>

    val constants: List<Constant<Double>>


    val parser: CalculationParser<Double>

    val lexer: CalculationLexer<Double>

    private const val PRECISION = 1e-7

    const val calculationError = "error"

    const val calculationDivideByZero = "zero"

    val numberFormatter = DecimalFormat("#.#######")

    // Checkers

    fun isNumberPart(c: Char): Boolean {
        return c in '0'..'9'
    }

    fun isFloatingPointSymbol(c: Char): Boolean {
        return c == '.' || c == ','
    }

    fun isBinaryOperationSymbol(operation: String): Boolean {
        return findTypedOperation<BinaryOperation<Double>>(operation) != null
    }

    fun isBinaryOperationSymbol(c: Char): Boolean {
        return isBinaryOperationSymbol(c.toString())
    }

    inline fun <reified T : Operation<Double>> startsWithOperation(text: String): Boolean {
        return operations.filterIsInstance(T::class.java).any { operation ->
            operation.textRepresentations.any { text.startsWith(it) }
        }
    }


    // Brackets helper (add missing and remove excessive ones)

    fun addRemoveBrackets(expression: String): String {
        var openingBrackets = 0
        var closingBrackets = 0
        for (char in expression) {
            if (char == '(') openingBrackets++
            if (char == ')') closingBrackets++
        }
        val sb = StringBuilder(expression)
        if (openingBrackets > closingBrackets) {
            sb.append(")".repeat(openingBrackets - closingBrackets))
        }
        val fixedExpression = sb.toString()
        return removeUnnecessaryBrackets(fixedExpression)
    }

    private fun countBrackets(expr: String): Int {
        var res = 0
        for (c in expr.toCharArray()) {
            if (c == ')') res++
        }
        return res
    }

    private fun getLastUnclosedBracket(open: IntArray, close: IntArray): Int {
        for (i in open.indices.reversed()) {
            if (open[i] != -1 && close[i] == -1) return i
        }
        return -1
    }

    private fun removeUnnecessaryBrackets(toFix: String): String {
        var brFixed = toFix
        var j = 0
        var obc = 0
        var brs = countBrackets(toFix)
        var ops2 = IntArray(brs)
        var cls2 = IntArray(brs)
        for (i in 0 until brs) {
            ops2[i] = -1
            cls2[i] = -1
        }
        var bw = ""
        while (j < brFixed.length) {
            if (brFixed.toCharArray()[j] == '(') ops2[obc++] = j
            if (brFixed.toCharArray()[j] == ')') {
                val lastOpenBr = getLastUnclosedBracket(ops2, cls2)
                if (lastOpenBr == -1) break
                val op = ops2[lastOpenBr]
                cls2[lastOpenBr] = j
                val betw = brFixed.substring(op + 1, j)
                if (betw == "($bw)") {
                    brFixed = brFixed.substring(0, op + 1) + bw + brFixed.substring(j)
                    j = -1
                    obc = 0
                    brs = countBrackets(toFix)
                    ops2 = IntArray(brs)
                    cls2 = IntArray(brs)
                    for (i in 0 until brs) {
                        ops2[i] = -1
                        cls2[i] = -1
                    }
                }
                bw = betw
            }
            j++
        }
        if (brFixed == "($bw)") brFixed = bw
        return brFixed
    }

    // Calculation utils

    fun calculateForceNumber(expression: String, angleType: AngleType = AngleType.RADIANS): Double {
        return calculate(expression, angleType) ?: 0.0
    }

    fun calculateForDisplay(expression: String, angleType: AngleType = AngleType.RADIANS): String {
        val result = calculate(expression, angleType) ?: return calculationError

        if (result.isInfinite()) return calculationDivideByZero

        return numberFormatter.format(result)
    }

    fun calculateForDisplay(context: Context, expression: String, angleType: AngleType = AngleType.RADIANS): String {

        return when (val result = calculateForDisplay(expression, angleType)) {
            calculationDivideByZero -> context.getString(PrefsHelper.getZeroDivResult())
            calculationError -> context.getString(R.string.error)
            else -> result
        }
    }

    @JvmStatic
    @JvmOverloads
    fun calculate(expression: String, angleType: AngleType = AngleType.RADIANS): Double? {
        return calculateWithStack(expression, angleType)
    }

    private fun calculateWithStack(expression: String, angleType: AngleType = AngleType.RADIANS): Double? {
        return parser.parse(expression, angleType, lexer).compute()
    }

    fun hasSignsInExpression(expr: String): Boolean {
        for (c in expr.toCharArray()) {
            if (isBinaryOperationSymbol(c)) {
                return true
            }
        }
        return false
    }

    private inline fun <reified T : Operation<Double>> findTypedOperation(textRepresentation: String): T? {
        return operations.filterIsInstance(T::class.java).find { it.textRepresentations.contains(textRepresentation) }
    }


    // Double Operations

    // Postfix operations
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

    // Extensions
    private inline val Double.isZero: Boolean
        get() = abs(this) < PRECISION

    init {

        operations = listOf(
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

        constants = listOf(
                Constant("e", Math.E),
                Constant(listOf("π", "pi"), Math.PI),
                Constant("φ", 1.6180339887),
        )

        parser = CalculationParser(DoubleParserConfiguration(operations, constants))

        lexer = CalculationLexer(DoubleLexerConfiguration(operations, constants))
    }
}