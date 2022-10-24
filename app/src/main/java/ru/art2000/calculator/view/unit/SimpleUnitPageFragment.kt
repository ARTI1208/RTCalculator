package ru.art2000.calculator.view.unit

import android.content.Intent
import android.text.Editable
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitFragSimpleBinding
import ru.art2000.calculator.model.unit.ConverterFunctions
import ru.art2000.calculator.model.unit.CopyMode
import ru.art2000.extensions.arch.launchAndCollect
import ru.art2000.extensions.arch.launchRepeatOnStarted
import ru.art2000.extensions.views.*

@AndroidEntryPoint
class SimpleUnitPageFragment : BaseUnitPageFragment<UnitFragSimpleBinding>() {

    companion object {

        const val MENU_ITEM_COPY = 0

    }

    private var spinnerFromPosition = 0
    private var spinnerToPosition = 1

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragSimpleBinding {
        return UnitFragSimpleBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        model.updateLocaleSpecific()
        binding.valueOriginal.setText(model.expression) // required to correctly place selection
        binding.valueOriginal.hint = converterFunctions.defaultValueString

        binding.buttonDot.text = model.decimalSeparator.toString()

        binding.buttonN.background = binding.buttonDot.background?.constantState?.newDrawable()
        ImageViewCompat.setImageTintList(binding.buttonN, binding.buttonDot.textColors)

        setSimpleViewButtonsClickListener()

        val textWatcher = object : SimpleTextWatcher() {

            override fun afterTextChanged(s: Editable) {
                model.liveExpression.value = s.toString()
            }
        }
        binding.valueOriginal.addTextChangedListener(textWatcher)
        binding.valueOriginal.isSaveEnabled = false

        binding.orHsv.autoScrollOnInput(viewLifecycleOwner.lifecycle)

        launchRepeatOnStarted {
            launchAndCollect(model.liveExpression) { expression ->
                if (expression != binding.valueOriginal.text?.toString()) {
                    binding.valueOriginal.setText(expression)
                }

                updateResult(binding.spinnerFrom.selectedItemPosition, expression)
            }

            launchAndCollect(model.liveInputSelection) { (first, second) ->
                binding.valueOriginal.setSelection(first, second)
            }
        }

        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding.valueOriginal.onSelectionChangedListener = null
                binding.valueOriginal.removeTextChangedListener(textWatcher)
            }
        })

        binding.valueOriginal.onSelectionChangedListener = CalculatorEditText.OnSelectionChangedListener { selStart, selEnd ->
            model.inputSelection = Pair(selStart, selEnd)
        }

        binding.swapButton.setOnClickListener {
            val newScaleX = if (it.scaleX >= 0) -1f else 1f
            it.animate().scaleX(newScaleX)
            binding.spinnerFrom.setSelection(binding.spinnerTo.selectedItemPosition)
        }

        val spinnerAdapter = createSpinnerAdapter()
        binding.spinnerFrom.adapter = spinnerAdapter
        binding.spinnerTo.adapter = spinnerAdapter

        binding.spinnerFrom.setSelection(spinnerFromPosition)
        binding.spinnerTo.setSelection(spinnerToPosition)

        binding.spinnerFrom.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                binding.originalDimensionHint.text = spinnerAdapter.getItem(selectedItemPosition)?.toString()
                if (selectedItemPosition == spinnerToPosition) {
                    model.setExpression(converterFunctions.displayValue(selectedItemPosition))
                    binding.spinnerTo.setSelection(spinnerFromPosition)
                }
                spinnerFromPosition = selectedItemPosition
                updateResult(binding.spinnerFrom.selectedItemPosition, binding.valueOriginal.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spinnerTo.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                binding.convertedDimensionHint.text = spinnerAdapter.getItem(selectedItemPosition)?.toString()
                if (selectedItemPosition == spinnerFromPosition) binding.spinnerFrom.setSelection(spinnerToPosition)
                spinnerToPosition = selectedItemPosition
                updateResult(binding.spinnerFrom.selectedItemPosition, binding.valueOriginal.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        registerForContextMenu(binding.valueConverted)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy_value).setOnMenuItemClickListener {
            copy(CopyMode.VALUE_ONLY)
        }

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy_with_short_name).setOnMenuItemClickListener {
            copy(CopyMode.VALUE_AND_SHORT_NAME)
        }

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy_with_full_name).setOnMenuItemClickListener {
            copy(CopyMode.VALUE_AND_FULL_NAME)
        }

    }

    private fun copy(copyMode: CopyMode): Boolean {

        val convertToItem = items[binding.spinnerTo.selectedItemPosition]

        return model.copy(
            binding.valueConverted.text,
            getString(convertToItem.shortNameResourceId),
            getString(convertToItem.nameResourceId),
            copyMode,
        )
    }

    private fun setSimpleViewButtonsClickListener() {

        val numberButtons = arrayOf(
            binding.button9, binding.button8, binding.button7,
            binding.button6, binding.button5, binding.button4,
            binding.button3, binding.button2, binding.button1,
            binding.button0, binding.button00
        )

        numberButtons.forEach { button ->
            button.setOnClickListener { model.handleNumber(button.text) }
        }

        binding.buttonMinus.setOnClickListener { model.onMinusClick() }

        binding.buttonClear.setOnClickListener { model.clearInput() }
        binding.buttonDel.setOnClickListener { model.deleteLastCharacter() }

        binding.buttonDot.setOnClickListener { model.handleFloatingPointSymbol() }

        binding.buttonN.setOnClickListener {
            val intent = Intent(requireContext(), AllUnitsActivity::class.java)
            intent.putExtra("highlightPosition", binding.spinnerFrom.selectedItemPosition)
            intent.putExtra("category", category)
            requireContext().startActivity(intent)
        }
    }

    private fun updateResult(position: Int, value: String) {
        converterFunctions.setValue(position, value, object : ConverterFunctions.ValueCallback {
            override fun shouldSkip(i: Int) = i == position
        })

        binding.valueConverted.textValue = converterFunctions.displayValue(binding.spinnerTo.selectedItemPosition)
    }

}