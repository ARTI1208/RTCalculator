package ru.art2000.calculator.unit.functions

import ru.art2000.calculator.unit.model.UnitConverterItem
import ru.art2000.calculator.calculator.computation.Calculations

internal class CategoryFunctions<T>(
    private val calculations: Calculations<T>,
    private val values: List<UnitConverterItem<T>>,
    private val defaultValue: T,
    private val store: MutableMap<String, Any>,
) : ConverterFunctions {

    override val defaultValueString: String
        get() = calculations.format(defaultValue)

    override fun isSet(position: Int): Boolean {
        return values[position].isSet
    }

    override fun setValue(
        position: Int,
        value: String,
        callback: ConverterFunctions.ValueCallback?,
    ) {
        val from = values[position]
        val number = calculations.calculate(value) ?: defaultValue
        from.setValue(number)
        repeat(values.size) { i ->
            if (callback?.shouldSkip(i) == true) return@repeat

            val convertedValue = values[i].convert(from)
            callback?.onValueUpdated(i) { calculations.format(convertedValue) }
        }
    }

    override fun displayValue(position: Int): String {
        return calculations.format(values[position].currentValue)
    }

    override fun storeInt(key: String, value: Int) {
        store[key] = value
    }

    override fun getInt(key: String, defaultValue: Int) = store.getOrElse(key) { defaultValue } as Int
}