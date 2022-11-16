package ru.art2000.calculator.calculator.preferences

import ru.art2000.calculator.calculator.model.DivideByZero
import ru.art2000.extensions.preferences.*

internal class CommonCalculatorPreferenceHelper(preferences: AppPreferences) : CalculatorPreferenceHelper {

    override val zeroDivResultProperty = run {

        val storeMapping = object : StoreMapping<DivideByZero, Boolean> {
            override fun Boolean.toOperate() = when(this) {
                true -> DivideByZero.INFINITY
                false -> DivideByZero.ERROR
            }

            override fun DivideByZero.toStore() = when(this) {
                DivideByZero.ERROR -> false
                DivideByZero.INFINITY -> true
            }

            override fun AppPreferences.get(key: String, defaultValue: Boolean): Boolean {
                return getBoolean(key, defaultValue)
            }

            override fun AppPreferences.set(key: String, value: Boolean) {
                return putBoolean(key, value)
            }
        }

        preferences.booleanPreference(
            CalculatorKeys.KEY_ZERO_DIVISION, with(storeMapping) {
                CalculatorDefaults.DEFAULT_ZERO_DIVISION.toStore()
            }
        ).mapOperate(storeMapping)
    }

    override var zeroDivResult by zeroDivResultProperty

    override var lastExpression by preferences.nullableStringPreference("lastExpression")

    override var lastExpressionWasCalculated by preferences.booleanPreference(
        "lastExpressionWasCalculated", false
    )

    override var lastMemory by preferences.stringPreference("lastMemory", "0")

}