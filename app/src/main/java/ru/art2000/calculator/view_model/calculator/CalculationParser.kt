package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.*
import java.util.*
import kotlin.collections.ArrayDeque

class CalculationParser<CalculationNumber>(
        private val configuration: ParserConfiguration<CalculationNumber>
) {

    fun parse(expression: String, angleType: AngleType, lexer: CalculationLexer<CalculationNumber>): Computable<CalculationNumber> {
        val array = expression.toCharArray()

        val lexemes = lexer.getLexemes(array) ?: return ErrorComputable

        return fromLexemes(lexemes, angleType)
    }

    fun fromLexemes(lexemes: List<ExpressionPart<CalculationNumber>>, angleType: AngleType): Computable<CalculationNumber> {
        val lexemesInPolishNotation = toPolishNotation(lexemes) ?: return ErrorComputable

        return Computable { calculate(lexemesInPolishNotation, angleType) }
    }

    private fun toPolishNotation(lexemes: List<ExpressionPart<CalculationNumber>>): ArrayDeque<ExpressionPart<CalculationNumber>>? {

        val queue = ArrayDeque<ExpressionPart<CalculationNumber>>()
        val stack: Deque<ExpressionPart<CalculationNumber>> = LinkedList()

        lexemes.forEach { lexeme ->
            when (lexeme) {
                is ExpressionValue, is Constant -> queue += lexeme
                is PrefixOperation -> stack.push(lexeme)
                is Operation -> {
                    var stackTop = stack.peek()
                    while (stackTop is Operation && stackTop.priorityLevel >= lexeme.priorityLevel) {
                        stack.pop()
                        queue += stackTop
                        stackTop = stack.peek()
                    }
                    stack.push(lexeme)
                }
                is BlockOpenExpression -> stack.push(lexeme)
                is BlockCloseExpression -> {
                    if (stack.isEmpty()) return null

                    var stackTop = stack.pop()

                    while (stackTop !is BlockOpenExpression) {
                        queue += stackTop

                        if (stack.isEmpty()) {
                            return null
                        }
                        stackTop = stack.pop()
                    }

                    stackTop = stack.peekFirst()
                    if (stackTop is PrefixOperation) {
                        stack.removeFirst()
                        queue += stackTop
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.pop()
            if (top !is Operation) return null
            queue += top
        }

        return queue
    }

    private fun calculate(lexemesInPolishNotation: ArrayDeque<ExpressionPart<CalculationNumber>>, angleType: AngleType): CalculationNumber? {

        val stack: Deque<CalculationNumber> = java.util.ArrayDeque()

        while (lexemesInPolishNotation.isNotEmpty()) {
            val lastToken = lexemesInPolishNotation.removeFirst()

            if (lastToken is ExpressionValue || lastToken is Constant) {
                val value = lastToken.value ?: return null

                if (lexemesInPolishNotation.isEmpty()) return value

                stack.push(value)

            } else if (lastToken !is Operation) {
                return null
            } else {

                when (lastToken) {
                    is BinaryOperation -> {
                        val rightOperand = stack.pollFirst() ?: return null
                        val leftOperand = stack.pollFirst() ?: return null

                        val result = lastToken.invoke(leftOperand, rightOperand) ?: return null

                        stack.push(result)
                    }
                    is PrefixOperation -> {

                        val operand = stack.pollFirst() ?: return null

                        val angleFixedOperand = if (lastToken.isTrigonometryOperation)
                            configuration.angleToRadiansConverter(operand, angleType)
                        else
                            operand

                        val result = lastToken.invoke(angleFixedOperand) ?: return null

                        stack.push(result)

                    }
                    is PostfixOperation -> {
                        val operand = stack.pollFirst() ?: return null

                        val result = lastToken.invoke(operand) ?: return null

                        stack.push(result)
                    }
                }
            }

        }

        if (stack.size != 1) return null

        return stack.pollFirst()
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