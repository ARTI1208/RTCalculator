package ru.art2000.calculator.calculator.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.art2000.calculator.calculator.R
import ru.art2000.extensions.preferences.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidCalculatorPreferenceHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CalculatorPreferenceHelper {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    // Calculator ========================================================

    override val zeroDivResult by preferences.booleanPreference(
        CalculatorKeys.KEY_ZERO_DIVISION, CalculatorDefaults.DEFAULT_ZERO_DIVISION
    ).getAs { if (it) R.string.infinity else R.string.error }

    override var lastExpression by preferences.nullableStringPreference(
        "lastExpression"
    )

    override var lastExpressionWasCalculated by preferences.booleanPreference(
        "lastExpressionWasCalculated", false
    )
    override var lastMemory by preferences.stringPreference(
        "lastMemory", "0"
    )

}