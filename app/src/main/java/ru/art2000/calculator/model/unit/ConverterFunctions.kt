package ru.art2000.calculator.model.unit

interface ConverterFunctions {

    val items: List<UnitItem>

    val defaultValueString: String

    fun isSet(position: Int): Boolean

    fun setValue(position: Int, value: String, callback: ValueCallback? = null)

    fun displayValue(position: Int): String

    interface ValueCallback {

        fun shouldSkip(i: Int): Boolean = false

        fun onValueUpdated(i: Int, newValueGetter: () -> String) {}

    }

    fun storeInt(key: String, value: Int)

    fun getInt(key: String, defaultValue: Int): Int
}