package ru.art2000.calculator.model.unit

import ru.art2000.calculator.view_model.calculator.Calculations

interface ConverterFunctionsProvider {

    val calculations: Calculations<*>

    fun getConverterFunctions(category: UnitCategory): ConverterFunctions

}