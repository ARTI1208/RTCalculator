package ru.art2000.calculator.utils

import ru.art2000.calculator.model.calculator.*
import ru.art2000.calculator.view_model.calculator.CalculationClass
import ru.art2000.extensions.language.safeToDouble
import java.text.DecimalFormat
import java.text.NumberFormat

@Suppress("unused")
object TreeCalculations {

    @Suppress("UNCHECKED_CAST")
    private val binaryOperations : List<BinaryOperation<Double>> =
            (CalculationClass.operations.filterIsInstance(BinaryOperation::class.java) as List<BinaryOperation<Double>>)
                    .sortedByDescending { it.priority }

    private fun findOperation(textRepresentation: String): Operation<Double>? {
        return CalculationClass.operations.find { it.textRepresentations.contains(textRepresentation) }
    }

    private inline fun <reified T : Operation<Double>> findTypedOperation(textRepresentation: String): T? {
        return CalculationClass.operations.filterIsInstance(T::class.java).find { it.textRepresentations.contains(textRepresentation) }
    }

    private fun findConstant(symbol: String): Constant<Double>? {
        return CalculationClass.constants.find { constant -> constant.symbols.any { it == symbol } }
    }

    private val String.isInBrackets: Boolean
        get() = startsWith("(") && endsWith(")")

    private val Char.isNumberPart: Boolean
        get() = this in '0'..'9' || this == '.' || this == ','

    private fun String.isInBrackets(toLeftOf: Int, toRightOf: Int): Boolean {
        if (toLeftOf < 0 || toRightOf > lastIndex) return false

        var currentCheckPosition = toLeftOf

        while (currentCheckPosition >= 0 && this[currentCheckPosition].isNumberPart) {
            --currentCheckPosition
        }

        if (currentCheckPosition < 0 || this[currentCheckPosition] != '(') return false

        currentCheckPosition = toRightOf

        while (currentCheckPosition < length && this[currentCheckPosition].isNumberPart) {
            ++currentCheckPosition
        }

        if (currentCheckPosition > lastIndex || this[currentCheckPosition] != ')') return false

        return true
    }

    private fun translateOperandToRadians(operation: UnaryOperation<Double>, angleType: AngleType, operand: Double): Double {
        return if (operation.isTrigonometryOperation)
            translateToRadians(operand, angleType)
        else
            operand
    }

    private fun translateToRadians(originalValue: Double, from: AngleType): Double {
        return when (from) {
            AngleType.DEGREES -> originalValue * Math.PI / 180
            AngleType.RADIANS -> originalValue
        }
    }

    fun parseExpression(expression: String, angleType: AngleType): CalculationTree<Double>? {

        val modifiedExpression = if (expression.isInBrackets)
            expression.substring(1, expression.lastIndex)
        else
            expression

        var operationFound: BinaryOperation<Double>? = null
        var operationSign = ""
        var index = 0

        for (operation in binaryOperations) {
            for (sign in operation.textRepresentations) {
                val indexTmp = modifiedExpression.indexOf(sign)
                if (indexTmp >= 0) {

                    if (modifiedExpression.isInBrackets(indexTmp - 1, indexTmp + sign.length)) {
                        continue
                    }

                    index = indexTmp
                    operationSign = sign
                    operationFound = operation
                    break
                }
            }
            if (operationFound != null) break
        }

        return when(operationFound) {
            null -> {

                println("Constant: $modifiedExpression")

                val postfixOperationPair = getEndingOperation<PostfixOperation<Double>>(modifiedExpression)

                val prefixOperationPair = getStartingOperation<PrefixOperation<Double>>(modifiedExpression)

                if (prefixOperationPair != null) {
                    val (prefixOperation, sign) = prefixOperationPair

                    val after = modifiedExpression.drop(sign.length)

                    val subTree = parseExpression(after, angleType) ?: return null

                    PrefixOperationTree(prefixOperation, subTree)
                } else {

                    val constant = findConstant(modifiedExpression)
                    if (constant == null) {
                        val doubleValue = modifiedExpression.safeToDouble() ?: return null
                        ConstantTree(doubleValue)
                    } else {
                        ConstantTree(constant.value)
                    }
                }
            }
            else -> {
                val before = modifiedExpression.substring(0, index)
                val after = modifiedExpression.substring(index + operationSign.length)

//                CalculationClass.println("Binarka", before + "|" + after)

                val leftTree = parseExpression(before, angleType) ?: return null
                val rightTree = parseExpression(after, angleType) ?: return null

                BinaryOperationTree(operationFound, leftTree, rightTree)
            }
//            is PrefixOperation -> {
//                val after = modifiedExpression.substring(index + operationSign.length)
//
//                println("After", after)
//
//                val subTree = parseExpression(after, angleType) ?: return null
//                val prefixTree = PrefixOperationTree(operationFound, subTree) { translateOperandToRadians(operationFound, angleType, it) }
//
//                if (after.isEmpty()) {
//                    prefixTree
//                } else {
//
//                }
//            }
//            is PostfixOperation -> {
//                val before = modifiedExpression.substring(0, index)
//
//                println("Beforee", before)
//
//                val subTree = parseExpression(before, angleType) ?: return null
//                PostfixOperationTree(operationFound, subTree) { translateOperandToRadians(operationFound, angleType, it) }
//            }
        }
    }

    private fun translateToRadians(str: String, from: AngleType): Double {
        val doubleValue = str.toDouble()
        return translateToRadians(doubleValue, from)
    }

    fun calculateWithTree(expression: String, angleType: AngleType = AngleType.DEGREES): String {
        val tree = parseExpression(expression, angleType) ?: return "error"
        val nf: NumberFormat = DecimalFormat("#.#######")
        return nf.format(tree())
    }

    private inline fun <reified T : Operation<Double>> getStartingOperation(text: String): Pair<T, String>? {
        var sign: String? = null
        val operation = CalculationClass.operations.filterIsInstance(T::class.java).find { operation ->
            sign = operation.textRepresentations.find {
                text.startsWith(it)
            }
            sign != null
        }

        return if (operation != null) operation to sign!! else null
    }

    private inline fun <reified T : Operation<Double>> getEndingOperation(text: String): Pair<T, String>? {
        var sign: String? = null
        val operation = CalculationClass.operations.filterIsInstance(T::class.java).find { operation ->
            sign = operation.textRepresentations.find {
                text.endsWith(it)
            }
            sign != null
        }

        return if (operation != null) operation to sign!! else null
    }

    private val Operation<Double>.priorityLevel: Int
        get() = when (this) {
            is PostfixOperation -> Int.MAX_VALUE
            is PrefixOperation -> Int.MIN_VALUE
            is BinaryOperation -> this.priority
        }
}