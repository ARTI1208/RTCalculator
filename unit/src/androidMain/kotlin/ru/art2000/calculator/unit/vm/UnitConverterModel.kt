package ru.art2000.calculator.unit.vm

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
import ru.art2000.calculator.calculator.computation.localizeExpression
import ru.art2000.calculator.calculator.vm.ExpressionInputViewModel
import ru.art2000.calculator.unit.functions.ConverterFunctionsProvider
import ru.art2000.calculator.unit.model.CopyMode
import ru.art2000.calculator.unit.model.UnitCategory
import ru.art2000.calculator.unit.view.BaseUnitPageFragment.Companion.CONVERT_FROM_KEY
import ru.art2000.extensions.arch.context
import java.text.DecimalFormatSymbols
import javax.inject.Inject
import ru.art2000.calculator.common.R as CommonR

@HiltViewModel
internal class UnitConverterModel @Inject constructor(
    @ApplicationContext application: Context,
    functionsProvider: ConverterFunctionsProvider<Int>,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application as Application), ExpressionInputViewModel {

    val category = savedStateHandle.get<UnitCategory>(CATEGORY_KEY)!!

    val converterFunctions = functionsProvider.getConverterFunctions(category)

    val converterNames = functionsProvider.getConverterItemNames(category)

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

    override val calculations = functionsProvider.calculations

    override var decimalSeparator: Char = DecimalFormatSymbols.getInstance().decimalSeparator
        private set(value) {
            val oldValue = decimalSeparator
            if (oldValue == value) return

            field = value
            liveExpression.update { localizeExpression(it, value) }
        }

    override fun updateLocaleSpecific() {
        val symbols = DecimalFormatSymbols.getInstance()
        decimalSeparator = symbols.decimalSeparator
    }

    fun onMinusClick() {
        val input = expression
        if (input != "") {
            expression = if (input.startsWith('-')) {
                input.substring(1)
            } else {
                val txt = "-$input"
                txt
            }
        }
    }

    fun copy(
        value: CharSequence,
        shortName: CharSequence,
        fullName: CharSequence,
        copyMode: CopyMode,
    ): Boolean {
        val cmg = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copiedText = when (copyMode) {
            CopyMode.VALUE_AND_SHORT_NAME -> "$value $shortName"
            CopyMode.VALUE_AND_FULL_NAME -> "$value $fullName"
            CopyMode.VALUE_ONLY -> value
        }
        val clipData = ClipData.newPlainText("unitConvertResult", copiedText)
        cmg.setPrimaryClip(clipData)

        // API 33+ features UI showing copied content, so skip toasts for them
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            val toastText = context.getString(CommonR.string.copied) + " " + copiedText
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }

        return true
    }

    companion object {
        private const val CATEGORY_KEY = "category"
    }
}