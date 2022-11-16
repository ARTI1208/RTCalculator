package ru.art2000.calculator.unit.vm

import ru.art2000.calculator.calculator.vm.ExpressionInputViewModel
import ru.art2000.calculator.unit.functions.ConverterFunctions
import ru.art2000.calculator.unit.model.CopyMode
import ru.art2000.calculator.unit.model.DisplayableUnitItem

internal interface IUnitConverterModel<D> : ExpressionInputViewModel {

    val converterFunctions: ConverterFunctions

    val converterNames: List<DisplayableUnitItem<D>>

    fun onMinusClick() {
        val input = expression
        if (input != "") {
            expression = if (input.startsWith('-')) {
                input.substring(1)
            } else {
                val txt = "-$input"
                txt
            }
        }
    }

    fun copy(
        value: CharSequence,
        shortName: CharSequence,
        fullName: CharSequence,
        copyMode: CopyMode,
    ): Boolean

}