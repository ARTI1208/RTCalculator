package ru.art2000.calculator.calculator.vm

internal interface ICalculatorModel : HistoryViewModel, ExpressionInputViewModel {

    fun handleConstant(constant: CharSequence)

    fun appendBinaryOperationSign(sign: CharSequence)

    fun handlePrefixUnaryOperationSign(sign: CharSequence)

    fun handlePostfixUnaryOperationSign(sign: CharSequence)

    fun handleMemoryOperation(operation: CharSequence)

    fun handleOpeningBracket()

    fun handleClosingBracket()

    fun onResult()

    fun clearResult()

    fun changeAngleType(): String

}