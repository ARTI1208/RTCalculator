package ru.art2000.calculator.model.calculator

sealed class Lexeme<O> {

}

class LexemeBlock<O> : Lexeme<O>() {

    val lexemes: MutableList<Lexeme<O>> = mutableListOf()

    operator fun plusAssign(lexeme: Lexeme<O>) {
        lexemes += lexeme
    }
}

class NumberLexeme<O>(val number: O) : Lexeme<O>() {
    override fun toString(): String {
        return number.toString()
    }
}

class OperationLexeme<O>(val operation: Operation<O>) : Lexeme<O>() {
    override fun toString(): String {
        return "'${operation.textRepresentations.first()}' [${operation.javaClass.simpleName}]"
    }
}

class ConstantLexeme<O>(val constant: Constant<O>) : Lexeme<O>() {
    override fun toString(): String {
        return constant.symbols.first()
    }
}

class OpeningBracketLexeme<O> : Lexeme<O>() {
    override fun toString(): String {
        return "("
    }
}

class ClosingBracketLexeme<O> : Lexeme<O>() {
    override fun toString(): String {
        return ")"
    }
}