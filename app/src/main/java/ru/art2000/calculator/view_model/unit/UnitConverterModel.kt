package ru.art2000.calculator.view_model.unit

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.art2000.calculator.R
import ru.art2000.calculator.model.unit.*
import ru.art2000.calculator.view_model.ExpressionInputViewModel
import ru.art2000.calculator.view_model.ExpressionInputViewModel.Companion.one
import ru.art2000.extensions.arch.context
import javax.inject.Inject

@HiltViewModel
class UnitConverterModel @Inject constructor(
    @ApplicationContext application: Context,
    private val functionsProvider: ConverterFunctionsProvider,
) : AndroidViewModel(application as Application), ExpressionInputViewModel {

    override val liveExpression = createLiveExpression(one)

    override val liveInputSelection = createLiveInput()

    override val calculations = functionsProvider.calculations

    fun getConverterFunctions(category: UnitCategory) = functionsProvider.getConverterFunctions(category)

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

        // API 33+ features UI showing copied content, so skip toasts for them
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            val toastText = context.getString(R.string.copied) + " " + copiedText
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
        }

        return true
    }
}