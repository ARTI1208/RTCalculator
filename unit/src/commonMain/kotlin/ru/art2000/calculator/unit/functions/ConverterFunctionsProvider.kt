package ru.art2000.calculator.unit.functions

import ru.art2000.calculator.unit.model.UnitCategory
import ru.art2000.calculator.calculator.computation.Calculations
import ru.art2000.calculator.unit.model.DisplayableUnitItem

internal interface ConverterFunctionsProvider<D> {

    val calculations: Calculations<*>

    fun getConverterFunctions(category: UnitCategory): ConverterFunctions

    fun getConverterItemNames(category: UnitCategory): List<DisplayableUnitItem<D>>

}