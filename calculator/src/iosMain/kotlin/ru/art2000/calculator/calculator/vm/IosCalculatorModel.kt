package ru.art2000.calculator.calculator.vm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import platform.Foundation.*
import platform.UIKit.UIPasteboard
import ru.art2000.calculator.calculator.computation.numbers.CalculatorFormatter
import ru.art2000.calculator.calculator.di.CalculatorHelper
import ru.art2000.calculator.calculator.model.AngleType
import ru.art2000.calculator.calculator.model.DivideByZero
import ru.art2000.calculator.calculator.model.HistoryListItem
import ru.art2000.calculator.calculator.repo.IosHistoryRepository

class IosCalculatorModel private constructor(
    private val delegate: CalculatorCommonModel,
): ICalculatorModel by delegate {

    constructor() : this(

        CalculatorHelper().calculatorPreferenceHelper.let { prefs ->
            CalculatorCommonModel(
                prefs,
                IosHistoryRepository(),
                CoroutineScope(Job() + Dispatchers.Default),
                { UIPasteboard.generalPasteboard.string = it },
                {
                    val separator = NSLocale.currentLocale.decimalSeparator
                    check(separator.length == 1)
                    separator.first()
                },
                { when (prefs.zeroDivResult) {
                    DivideByZero.ERROR -> error
                    DivideByZero.INFINITY -> "∞"
                } },
                { error },
                CalculatorFormatter,
            )
        }
    )

    private val watchScope = CoroutineScope(Job() + Dispatchers.Main)

    fun watchExpression(onExpressionChanged: (String?) -> Unit) {
        liveExpression.onEach(onExpressionChanged).launchIn(watchScope)
    }

    fun watchResult(onResultChanged: (String?) -> Unit) {
        delegate.liveResult.onEach(onResultChanged).launchIn(watchScope)
    }

    fun watchAngleType(onTypeChanged: (AngleType) -> Unit) {
        delegate.liveAngleType.onEach(onTypeChanged).launchIn(watchScope)
    }

    fun watchMemory(onMemoryChanged: (String) -> Unit) {
        delegate.liveMemory.onEach(onMemoryChanged).launchIn(watchScope)
    }

    fun watchHistory(onHistoryChanged: (List<HistoryListItem>) -> Unit) {
        delegate.historyListItems.onEach(onHistoryChanged).launchIn(watchScope)
    }

    companion object {
        private val error
            get() = NSBundle.mainBundle.localizedStringForKey("Error", "Error", null)
    }
}