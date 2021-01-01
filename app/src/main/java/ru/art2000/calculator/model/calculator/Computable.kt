package ru.art2000.calculator.model.calculator

fun interface Computable<out CalculationNumber> {

    fun compute(): CalculationNumber?

}

object ErrorComputable : Computable<Nothing> {

    override fun compute(): Nothing? = null
}