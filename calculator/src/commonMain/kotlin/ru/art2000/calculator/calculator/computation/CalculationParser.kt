package ru.art2000.calculator.calculator.computation

import ru.art2000.calculator.calculator.computation.parts.*
import ru.art2000.calculator.calculator.model.AngleType

internal class CalculationParser<CalculationNumber>(
    private val configuration: ParserConfiguration<CalculationNumber>
) {

    fun parse(
        expression: String,
        angleType: AngleType,
        lexer: CalculationLexer<CalculationNumber>
    ): Computable<CalculationNumber> {
        val array = expression.toCharArray()

        val lexemes = lexer.getLexemes(array) ?: return ErrorComputable

        return fromLexemes(lexemes, angleType)
    }

    fun fromLexemes(
        lexemes: List<ExpressionPart<CalculationNumber>>,
        angleType: AngleType
    ): Computable<CalculationNumber> {
        val lexemesInPolishNotation = toPolishNotation(lexemes) ?: return ErrorComputable

        return Computable { calculate(lexemesInPolishNotation, angleType) }
    }

    private fun toPolishNotation(
        lexemes: List<ExpressionPart<CalculationNumber>>
    ): ArrayDeque<ExpressionPart<CalculationNumber>>? {

        val queue = ArrayDeque<ExpressionPart<CalculationNumber>>()
        val stack = ArrayDeque<ExpressionPart<CalculationNumber>>()

        lexemes.forEach { lexeme ->
            when (lexeme) {
                is ExpressionValue, is Constant -> queue += lexeme
                is PrefixOperation -> stack.addFirst(lexeme)
                is Operation -> {
                    var stackTop = stack.firstOrNull()
                    while (stackTop is Operation && stackTop.priorityLevel >= lexeme.priorityLevel) {
                        stack.removeFirst()
                        queue += stackTop
                        stackTop = stack.firstOrNull()
                    }
                    stack.addFirst(lexeme)
                }
                is BlockOpenExpression -> stack.addFirst(lexeme)
                is BlockCloseExpression -> {
                    if (stack.isEmpty()) return null

                    var stackTop = stack.removeFirst()

                    while (stackTop !is BlockOpenExpression) {
                        queue += stackTop

                        if (stack.isEmpty()) {
                            return null
                        }
                        stackTop = stack.removeFirst()
                    }

                    stackTop = stack.firstOrNull() ?: return@forEach
                    if (stackTop is PrefixOperation) {
                        stack.removeFirst()
                        queue += stackTop
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.removeFirst()
            if (top !is Operation) return null
            queue += top
        }

        return queue
    }

    private fun calculate(
        lexemesInPolishNotation: ArrayDeque<ExpressionPart<CalculationNumber>>,
        angleType: AngleType
    ): CalculationNumber? {

        val stack = ArrayDeque<CalculationNumber>()

        while (lexemesInPolishNotation.isNotEmpty()) {

            when (val lastToken = lexemesInPolishNotation.removeFirst()) {
                is ExpressionValue, is Constant -> {
                    val value = lastToken.value ?: return null

                    if (lexemesInPolishNotation.isEmpty()) return value

                    stack.addFirst(value)
                }
                !is Operation -> return null
                is BinaryOperation -> {
                    val rightOperand = stack.removeFirstOrNull() ?: return null
                    val leftOperand = stack.removeFirstOrNull() ?: return null

                    val result = lastToken(leftOperand, rightOperand) ?: return null

                    stack.addFirst(result)
                }
                is PrefixOperation -> {

                    val operand = stack.removeFirstOrNull() ?: return null

                    val angleFixedOperand = if (lastToken.isTrigonometryOperation)
                        configuration.angleToRadiansConverter(operand, angleType)
                    else
                        operand

                    val result = lastToken(angleFixedOperand) ?: return null

                    stack.addFirst(result)
                }
                is PostfixOperation -> {
                    val operand = stack.removeFirstOrNull() ?: return null

                    val result = lastToken(operand) ?: return null

                    stack.addFirst(result)
                }
            }
        }

        if (stack.size != 1) return null

        return stack.removeFirstOrNull()
    }

    private val <O> ExpressionPart<O>.value: O?
        get() = when (this) {
            is ExpressionValue -> value
            is Constant -> value
            else -> null
        }

    private val Operation<*>.priorityLevel: Int
        get() = when (this) {
            is PostfixOperation -> Int.MAX_VALUE
            is PrefixOperation -> Int.MIN_VALUE
            is BinaryOperation -> priority
        }

}