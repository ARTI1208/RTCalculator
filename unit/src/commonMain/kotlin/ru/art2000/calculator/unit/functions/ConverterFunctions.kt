package ru.art2000.calculator.unit.functions

internal interface ConverterFunctions {

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

    companion object {
        const val CONVERT_FROM_KEY = "from"
    }
}