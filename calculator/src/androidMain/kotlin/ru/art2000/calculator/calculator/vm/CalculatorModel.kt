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
) : AndroidViewModel(application as Application), HistoryViewModel, ExpressionInputViewModel {

    private val commonModel = CalculatorCommonModel(
        prefsHelper, historyRepository, viewModelScope,
        ::copyText, { DecimalFormatSymbols.getInstance().decimalSeparator },
        { context.getString(prefsHelper.zeroDivResult) },
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

    fun handlePrefixUnaryOperationSign(sign: CharSequence) =
        commonModel.handlePrefixUnaryOperationSign(sign)

    fun handlePostfixUnaryOperationSign(sign: CharSequence) =
        commonModel.handlePostfixUnaryOperationSign(sign)

    fun handleOpeningBracket() = commonModel.handleOpeningBracket()

    fun handleClosingBracket() = commonModel.handleClosingBracket()

    override fun clearInput() = commonModel.clearInput()

    override fun deleteLastCharacter() = commonModel.deleteLastCharacter()

    fun appendBinaryOperationSign(sign: CharSequence) = commonModel.appendBinaryOperationSign(sign)

    fun onResult() = commonModel.onResult()

    fun calculateAndFormatForDisplay(expression: String, angleType: AngleType) =
        commonModel.calculateAndFormatForDisplay(expression, angleType)

    fun handleMemoryOperation(operation: CharSequence) =
        commonModel.handleMemoryOperation(operation)

    fun handleConstant(constant: CharSequence) = commonModel.handleConstant(constant)

    override fun handleNumber(number: CharSequence) = commonModel.handleNumber(number)

    override fun handleFloatingPointSymbol() = commonModel.handleFloatingPointSymbol()

    fun clearResult() = commonModel.clearResult()

    fun changeAngleType() = commonModel.changeAngleType()
}