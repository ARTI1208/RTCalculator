package ru.art2000.calculator.view_model.calculator

import ru.art2000.calculator.model.calculator.LexerConfiguration
import ru.art2000.calculator.model.calculator.parts.*
import ru.art2000.extensions.language.*
import java.nio.CharBuffer

class DefaultCalculationLexer<CalculationNumber>(
    private val configuration: LexerConfiguration<CalculationNumber>
) : Any(), CalculationLexer<CalculationNumber> {

    private inline val operations: List<Operation<CalculationNumber>>
        get() = configuration.field.operations

    private inline val constants: List<Constant<CalculationNumber>>
        get() = configuration.field.constants

    private inline val numberConverter: (CharSequence) -> CalculationNumber?
        get() = configuration::numberConverter

//    enum class LexerState {
//        START,
//        BLOCK_OPENED,
//        BLOCK_CLOSED,
//        NUMBER,
//        CONSTANT,
//        PREFIX,
//        POSTFIX,
//        BINARY
//    }

    data class LexerData<CalculationNumber>(
        val expression: CharArray,
        val lexemes: MutableList<ExpressionPart<CalculationNumber>> = mutableListOf(),
        var isReadingNumber: Boolean = false,
        var isSignRead: Boolean = false,
        var startingIndex: Int = -1,
        var exponentStarted: Boolean = false,
    )

    sealed interface LexerState {

        abstract fun <CalculationNumber> process(
            data: LexerData<CalculationNumber>,
            index: Int,
            c: Char,
        ): LexerState?

    }

    private fun tryOpenBracket(
        data: LexerData<CalculationNumber>,
        index: Int,
        c: Char,
    ): Boolean {
        if (
//                !charProcessed &&
            c.isOpeningBracket) {
            if (data.startingIndex >= 0) {

                val part = data.expression.substring(data.startingIndex, index)

                val subLexemes = part.numberOrUnaryOrWords(data.isReadingNumber && (!data.isSignRead || data.lexemes.isEmpty()))
                if (subLexemes.isEmpty()) return false

                data.lexemes += subLexemes

                data.startingIndex = -1
                data.isReadingNumber = false
            }

            data.lexemes += blockOpen()
            return true
        }

        return false
    }

    object StartState : LexerState {

        // accepts parts of: prefix op, value based, bracket open
        override fun <CalculationNumber> process(
            data: LexerData<CalculationNumber>,
            index: Int,
            c: Char,
        ): LexerState? {

//            if (
////                !charProcessed &&
//                c.isOpeningBracket) {
//                if (data.startingIndex >= 0) {
//
//                    val part = data.expression.substring(data.startingIndex, index)
//
//                    val subLexemes = part.numberOrUnaryOrWords(data.isReadingNumber && (!data.isSignRead || data.lexemes.isEmpty()))
//                    if (subLexemes.isEmpty()) return null
//
//                    data.lexemes += subLexemes
//
//                    data.startingIndex = -1
//                    data.isReadingNumber = false
//                }
//
//                data.lexemes += blockOpen()
//                return BlockOpenState
//            }

            return null
        }
    }

    object BlockOpenState : LexerState {
        override fun <CalculationNumber> process(
            data: LexerData<CalculationNumber>,
            index: Int,
            c: Char
        ): LexerState? {
            TODO("Not yet implemented")
        }

    }


    override fun getLexemes(expression: CharArray): List<ExpressionPart<CalculationNumber>>? {

        val blockOpening = BlockOpenExpression<CalculationNumber>()
        val blockClosing = BlockCloseExpression<CalculationNumber>()

        val lexemes: MutableList<ExpressionPart<CalculationNumber>> = mutableListOf()

        var isReadingNumber = false
        var isSignRead = false
        var startingIndex = -1
        var exponentStarted = false

        var index = 0
        while (index < expression.length) {
            val c = expression[index]

            var charProcessed = false

            if (!charProcessed && c.isOpeningBracket) {
                if (startingIndex >= 0) {

                    val part = expression.substring(startingIndex, index)

                    val subLexemes = part.numberOrUnaryOrWords(isReadingNumber && (!isSignRead || lexemes.isEmpty()))
                    if (subLexemes.isEmpty()) return null

                    lexemes += subLexemes

                    startingIndex = -1
                    isReadingNumber = false
                }

                lexemes += blockOpening
                charProcessed = true
            }

            if (!charProcessed && c.isClosingBracket) {
                if (startingIndex >= 0) {

                    val part = expression.substring(startingIndex, index)
                    val subLexemes = part.numberOrUnaryOrWords(isReadingNumber && !isSignRead)
                    if (subLexemes.isEmpty()) return null

                    lexemes += subLexemes

                    startingIndex = -1
                    isReadingNumber = false
                }

                lexemes += blockClosing
                charProcessed = true
            }

            if (!charProcessed && c.isNumberPart) {
                if (c == ',') {
                    expression[index] = '.'
                }

                if (startingIndex == -1) {
                    if (c.isNumberSign && lexemes.isNotEmpty() && (lexemes.lastOrNull() !is BlockOpenExpression)) {
                        val cStr = c.toString()
                        val signOperation = operations.first { it is BinaryOperation<*> && it.textRepresentations.contains(cStr) }
                        lexemes += signOperation
                    } else {
                        startingIndex = index
                        isReadingNumber = true
                        exponentStarted = false
                        isSignRead = c.isNumberSign
                    }
                } else if (!isReadingNumber) {
                    val part = expression.substring(startingIndex, index)

                    val wordLexemes = part.wordLexemes()

                    if (wordLexemes.isEmpty()) return null

                    lexemes += wordLexemes

                    startingIndex = index
                    isReadingNumber = true
                    isSignRead = false
                } else if (c.isNumberSign && !exponentStarted) {

                    val sub = expression.substring(startingIndex, index)

                    val lastLexeme = lexemes.lastOrNull()

                    val unary = lastLexeme == null || lastLexeme is Operation || lastLexeme is BlockOpenExpression

                    val subLexemes = sub.numberOrUnaryOrWords(unary)
                    if (subLexemes.isEmpty()) return null
                    lexemes += subLexemes

                    startingIndex = index
                    isReadingNumber = subLexemes.lastOrNull() is Operation
                    isSignRead = true
                } else {
                    isSignRead = false
                }

                charProcessed = true
            }

            if (!charProcessed && c.isSpacing) {
                if (startingIndex >= 0 && !(isReadingNumber || isSignRead)) {

                    val part = expression.substring(startingIndex, index)

                    val subLexemes = part.numberOrUnaryOrWords(isReadingNumber)
                    if (subLexemes.isEmpty()) return null
                    lexemes += subLexemes

                    startingIndex = -1
                    isReadingNumber = false
                }
                charProcessed = true
            }

            if (!charProcessed && c.isScientific) {
                if (isReadingNumber) {
                    exponentStarted = true
                    charProcessed = true
                }
            }

            if (!charProcessed) {
                if (startingIndex == -1) {
                    startingIndex = index
                } else if (isReadingNumber) {
                    val part = expression.substring(startingIndex, index)
                    val double = part.numberLexeme() ?: kotlin.run {
                        println("Unknown number lexeme '${expression.substring(startingIndex, index)}'")
                        return null
                    }

                    lexemes += double
                    startingIndex = index
                }
                isReadingNumber = false
            }

            // fallback
            ++index
        }

        if (startingIndex >= 0) {
            val part = expression.substring(startingIndex, expression.length)

            val subLexemes = part.numberOrUnaryOrWords(isReadingNumber)
            if (subLexemes.isEmpty()) return null

            lexemes += subLexemes
        }

        return lexemes
    }

    private inline val Char.isNumberPart: Boolean
        get() = this in '0'..'9' || isFloatingPointSymbol || isNumberSign

    private fun CharSequence.numberLexeme(): ExpressionValue<CalculationNumber>?
        = numberConverter(replace(spaceRegex, ""))?.let { ExpressionValue(it) }

    private fun CharSequence.numberLexemeNoReplace(): ExpressionValue<CalculationNumber>? = numberConverter(this)?.let { ExpressionValue(it) }

    private fun CharSequence.indexOfUnaryOperation(): Int {
        val minusIndex = indexOf('-')
        if (minusIndex >= 0) return minusIndex

        return indexOf('+')
    }

    private fun CharSequence.numberOrUnaryOperationLexeme(): ExpressionPart<CalculationNumber>? {
        val unaryIndex = indexOfUnaryOperation()
        if (unaryIndex < 0 || (unaryIndex > 0 && this[unaryIndex - 1].isScientific)) return numberLexemeNoReplace()

        if (unaryIndex == lastIndex) return this[unaryIndex].signOperationLexeme()

        val replaced = substring(unaryIndex + 1)

        return numberConverter(replaced)?.let {
            val isNegation = this[unaryIndex] == '-'
            val result = (if (isNegation) configuration.field.negateOperation(it) else it) ?: return null
            ExpressionValue(result)
        } ?: this[unaryIndex].signOperationLexeme()
    }

    private fun Char.signOperationLexeme(): Operation<CalculationNumber>? {
        return when (this) {
            '+' -> configuration.field.identityOperation
            '-' -> configuration.field.negateOperation
            else -> null
        }
    }

    private fun CharSequence.wordLexemes(): List<ExpressionPart<CalculationNumber>> {
        val result = mutableListOf<ExpressionPart<CalculationNumber>>()

        var currentStart = 0

        fun skipSpacing() {
            while (currentStart < length && this[currentStart].isSpacing) {
                ++currentStart
            }
        }

        skipSpacing()

        mainCycle@ while (currentStart < length) {

            var anyFound = false

            fun compareTextRepresentation(textRepresentation: String, part: ExpressionPart<CalculationNumber>) {
                if (startsWith(textRepresentation, currentStart)) {
                    anyFound = true

                    result += part

                    currentStart += textRepresentation.length

                    skipSpacing()
                }
            }

            for (operation in operations) {
                if (operation is PrefixOperation && operation.isSignOperation) continue
                for (sign in operation.textRepresentations) {
                    compareTextRepresentation(sign, operation)
                    if (currentStart >= length) break@mainCycle
                }
            }

            for (constant in constants) {
                for (symbol in constant.symbols) {
                    compareTextRepresentation(symbol, constant)
                    if (currentStart >= length) break@mainCycle
                }
            }

            if (!anyFound) return emptyList()
        }

        return result
    }

    private fun CharSequence.numberOrUnaryOrWords(numberOrUnary: Boolean): List<ExpressionPart<CalculationNumber>> {
        return if (numberOrUnary)
            numberOrUnaryOperationLexeme()?.let { listOf(it) } ?: emptyList()
        else
            wordLexemes()
    }

    private fun CharArray.substring(from: Int, to: Int): CharSequence {
//        return CharBuffer.wrap(this, from, to - from)
        return String(this, from, to - from)
    }

    private inline val CharArray.length get() = size

}