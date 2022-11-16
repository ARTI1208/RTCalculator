package ru.art2000.calculator.unit.vm

import kotlinx.coroutines.flow.update
import ru.art2000.calculator.calculator.computation.localizeExpression
import ru.art2000.calculator.unit.functions.ConverterFunctions.Companion.CONVERT_FROM_KEY
import ru.art2000.calculator.unit.functions.ConverterFunctionsProvider
import ru.art2000.calculator.unit.model.CopyMode
import ru.art2000.calculator.unit.model.UnitCategory

internal class CommonUnitConverterModel<D>(
    functionsProvider: ConverterFunctionsProvider<D>,
    category: UnitCategory,
    private val copy: (String) -> Unit,
    private val getDecimalSeparator: () -> Char,
) : IUnitConverterModel<D> {

    override val converterFunctions = functionsProvider.getConverterFunctions(category)
    override val converterNames = functionsProvider.getConverterItemNames(category)

    override fun copy(
        value: CharSequence, shortName: CharSequence, fullName: CharSequence, copyMode: CopyMode
    ): Boolean {

        val copiedText = when (copyMode) {
            CopyMode.VALUE_AND_SHORT_NAME -> "$value $shortName"
            CopyMode.VALUE_AND_FULL_NAME -> "$value $fullName"
            CopyMode.VALUE_ONLY -> value.toString()
        }

        copy(copiedText)

        return true
    }

    override val liveExpression = createLiveExpression(run {
        var value = ""

        val index = converterFunctions.getInt(CONVERT_FROM_KEY, 0)

        if (converterFunctions.isSet(index)) {
            value = when (val v = converterFunctions.displayValue(index)) {
                converterFunctions.defaultValueString -> ""
                else -> v
            }
        }

        value
    })

    override val liveInputSelection = createLiveInput()

    override var decimalSeparator: Char = getDecimalSeparator()
        private set(value) {
            val oldValue = decimalSeparator
            if (oldValue == value) return

            field = value
            liveExpression.update { localizeExpression(it, value) }
        }

    override val calculations = functionsProvider.calculations

    override fun updateLocaleSpecific() {
        decimalSeparator = getDecimalSeparator()
    }
}