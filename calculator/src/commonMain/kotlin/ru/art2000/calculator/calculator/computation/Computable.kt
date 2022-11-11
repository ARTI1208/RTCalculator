package ru.art2000.calculator.calculator.computation

internal fun interface Computable<out CalculationNumber> {

    fun compute(): CalculationNumber?
}

internal object ErrorComputable : Computable<Nothing> {

    override fun compute(): Nothing? = null
}