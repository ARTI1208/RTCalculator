package ru.art2000.calculator.calculator.vm

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import ru.art2000.calculator.calculator.R
import ru.art2000.calculator.calculator.computation.CalculatorFormatter
import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.model.DivideByZero
import ru.art2000.calculator.calculator.model.HistoryValueItem
import ru.art2000.calculator.calculator.preferences.CalculatorPreferenceHelper
import ru.art2000.calculator.calculator.repo.HistoryRepository
import ru.art2000.extensions.arch.context
import java.text.DecimalFormatSymbols
import javax.inject.Inject

@HiltViewModel
internal class CalculatorModel @Inject constructor(
    @ApplicationContext application: Context,
    prefsHelper: CalculatorPreferenceHelper,
    historyRepository: HistoryRepository,
) : AndroidViewModel(application as Application), ICalculatorModel {

    private val commonModel = CalculatorCommonModel(
        prefsHelper, historyRepository, viewModelScope,
        ::copyText, { DecimalFormatSymbols.getInstance().decimalSeparator },
        {
            when (prefsHelper.zeroDivResult) {
                DivideByZero.ERROR -> R.string.error
                DivideByZero.INFINITY -> R.string.infinity
            }.let { context.getString(it) }
        },
        { context.getString(R.string.error) },
        CalculatorFormatter,
    )

    private fun copyText(text: String) {
        val cmg = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(null, text)
        cmg.setPrimaryClip(clip)
    }

    override val decimalSeparator = commonModel.decimalSeparator

    override val liveExpression = commonModel.liveExpression

    override val liveInputSelection = commonModel.liveInputSelection

    override val calculations = commonModel.calculations

    override fun updateLocaleSpecific() = commonModel.updateLocaleSpecific()

    val liveResult: StateFlow<String?> get() = commonModel.liveResult

    val liveMemory: StateFlow<String> get() = commonModel.liveMemory

    val liveAngleType: StateFlow<AngleType> = commonModel.liveAngleType

    override val historyListItems = commonModel.historyListItems

    override fun copyHistoryItemToClipboard(
        item: HistoryValueItem,
        type: HistoryViewModel.CopyMode
    ) = commonModel.copyHistoryItemToClipboard(item, type)

    override fun updateHistoryItem(item: HistoryValueItem) = commonModel.updateHistoryItem(item)

    override fun removeHistoryItem(item: HistoryValueItem) = commonModel.removeHistoryItem(item)

    override fun clearHistoryDatabase() = commonModel.clearHistoryDatabase()

    override fun ensureDisplayResult(historyValueItem: HistoryValueItem) =
        commonModel.ensureDisplayResult(historyValueItem)

    override fun handlePrefixUnaryOperationSign(sign: CharSequence) =
        commonModel.handlePrefixUnaryOperationSign(sign)

    override fun handlePostfixUnaryOperationSign(sign: CharSequence) =
        commonModel.handlePostfixUnaryOperationSign(sign)

    override fun handleOpeningBracket() = commonModel.handleOpeningBracket()

    override fun handleClosingBracket() = commonModel.handleClosingBracket()

    override fun clearInput() = commonModel.clearInput()

    override fun deleteLastCharacter() = commonModel.deleteLastCharacter()

    override fun appendBinaryOperationSign(sign: CharSequence) = commonModel.appendBinaryOperationSign(sign)

    override fun onResult() = commonModel.onResult()

    fun calculateAndFormatForDisplay(expression: String, angleType: AngleType) =
        commonModel.calculateAndFormatForDisplay(expression, angleType)

    override fun handleMemoryOperation(operation: CharSequence) =
        commonModel.handleMemoryOperation(operation)

    override fun handleConstant(constant: CharSequence) = commonModel.handleConstant(constant)

    override fun handleNumber(number: CharSequence) = commonModel.handleNumber(number)

    override fun handleFloatingPointSymbol() = commonModel.handleFloatingPointSymbol()

    override fun clearResult() = commonModel.clearResult()

    override fun changeAngleType() = commonModel.changeAngleType()
}