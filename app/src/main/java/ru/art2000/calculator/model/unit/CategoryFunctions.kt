package ru.art2000.calculator.model.unit

import ru.art2000.calculator.view_model.calculator.Calculations

class CategoryFunctions<T>(
    private val calculations: Calculations<T>,
    override val items: List<UnitConverterItem<T>>,
    private val defaultValue: T,
    private val store: MutableMap<String, Any>,
) : ConverterFunctions {

    override val defaultValueString: String
        get() = calculations.format(defaultValue)

    override fun isSet(position: Int): Boolean {
        return items[position].isSet
    }

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

    override fun storeInt(key: String, value: Int) {
        store[key] = value
    }

    override fun getInt(key: String, defaultValue: Int) = store.getOrDefault(key, defaultValue) as Int
}