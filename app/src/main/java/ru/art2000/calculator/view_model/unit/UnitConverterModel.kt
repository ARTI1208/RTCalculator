package ru.art2000.calculator.view_model.unit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.art2000.calculator.R
import ru.art2000.calculator.model.unit.CopyMode
import ru.art2000.calculator.view_model.ExpressionInputViewModel
import ru.art2000.calculator.view_model.ExpressionInputViewModel.Companion.one
import ru.art2000.calculator.view_model.calculator.DoubleCalculations

class UnitConverterModel : ViewModel(), ExpressionInputViewModel {

    override val liveExpression: MutableLiveData<String> = createExpressionLiveData(one)

    override val liveInputSelection: MutableLiveData<Pair<Int, Int>> = createInputLiveData()

    override val calculations = DoubleCalculations(UnitConverterFormatter)

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
            context: Context,
            value: CharSequence,
            shortName: CharSequence,
            fullName: CharSequence,
            copyMode: CopyMode
    ): Boolean {
        val cmg = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copiedText = when (copyMode) {
            CopyMode.VALUE_AND_SHORT_NAME -> "$value $shortName"
            CopyMode.VALUE_AND_FULL_NAME -> "$value $fullName"
            CopyMode.VALUE_ONLY -> value
        }
        val clipData = ClipData.newPlainText("unitConvertResult", copiedText)
        cmg.setPrimaryClip(clipData)

        val toastText = context.getString(R.string.copied) + " " + copiedText
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()

        return true
    }
}