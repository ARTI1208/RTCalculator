package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.parts.BinaryOperation
import ru.art2000.calculator.model.calculator.parts.Operation

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

fun Calculations<*>.isBinaryOperationSymbol(operation: String): Boolean {
    return findTypedOperation<BinaryOperation<*>>(operation) != null
}

fun Calculations<*>.isBinaryOperationSymbol(c: Char): Boolean {
    return isBinaryOperationSymbol(c.toString())
}

private inline fun <reified T : Operation<*>> Calculations<*>.findTypedOperation(textRepresentation: String): T? {
    return field.operations.filterIsInstance(T::class.java).find { it.textRepresentations.contains(textRepresentation) }
}

fun Calculations<*>.hasSignsInExpression(expr: String): Boolean {
    for (c in expr.toCharArray()) {
        if (isBinaryOperationSymbol(c)) return true
    }
    return false
}

inline fun <reified T : Operation<*>> Calculations<*>.startsWithOperation(text: String): Boolean {
    return field.operations.filterIsInstance(T::class.java).any { operation ->
        operation.textRepresentations.any { text.startsWith(it) }
    }
}