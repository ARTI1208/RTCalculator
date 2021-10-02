package ru.art2000.calculator.view_model.unit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
}