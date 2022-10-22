package ru.art2000.calculator.model.unit

import ru.art2000.calculator.view_model.calculator.Calculations

class CategoryFunctions<T>(
    private val calculations: Calculations<T>,
    override val items: List<UnitConverterItem<T>>,
    private val defaultValue: T,
) : ConverterFunctions {

    override val defaultValueString: String
        get() = calculations.format(defaultValue)

    override fun setValue(
        position: Int,
        value: String,
        callback: ConverterFunctions.ValueCallback?,
    ) {
        val from = items[position]
        val number = calculations.calculate(value) ?: defaultValue
        from.setValue(number)
        repeat(items.size) { i ->
            if (callback?.shouldSkip(i) == true) return@repeat

            val convertedValue = items[i].convert(from)
            callback?.onValueUpdated(i) { calculations.format(convertedValue) }
        }
    }

    override fun displayValue(position: Int): String {
        return calculations.format(items[position].currentValue)
    }
}