package ru.art2000.calculator.view.unit

import android.content.Intent
import android.text.Editable
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitFragSimpleBinding
import ru.art2000.calculator.model.unit.CopyMode
import ru.art2000.calculator.model.unit.UnitConverterItem
import ru.art2000.extensions.views.*
import ru.art2000.helpers.GeneralHelper

class SimpleUnitPageFragment : BaseUnitPageFragment<UnitFragSimpleBinding>() {

    companion object {

        const val MENU_ITEM_COPY = 0

        private const val DEFAULT_EMPTY_VALUE_INTERPRETATION = 1
    }

    private var spinnerFromPosition = 0
    private var spinnerToPosition = 1

    private var emptyValueInterpretation: Number = 0
        set(value) {
            field = value
            val binding = mBinding ?: return
            binding.valueOriginal.hint = GeneralHelper.resultNumberFormat.format(value)
        }

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragSimpleBinding {
        return UnitFragSimpleBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        val binding = mBinding ?: return

        binding.valueOriginal.setText(model.expression) // required to correctly place selection
        emptyValueInterpretation = DEFAULT_EMPTY_VALUE_INTERPRETATION

        setSimpleViewButtonsClickListener()

        binding.valueOriginal.addTextChangedListener(object : SimpleTextWatcher() {

            override fun afterTextChanged(s: Editable) {
                model.liveExpression.value = s.toString()
            }
        })

        binding.orHsv.autoScrollOnInput()

        model.liveExpression.observe(viewLifecycleOwner, { expression: String ->
            if (expression != binding.valueOriginal.text?.toString()) {
                binding.valueOriginal.setText(expression)
            }

            updateResult(binding.spinnerFrom.selectedItemPosition, expression)
        })

        model.liveInputSelection.observe(viewLifecycleOwner) { (first, second) ->
            binding.valueOriginal.setSelection(first, second)
        }

        binding.valueOriginal.onSelectionChangedListener = CalculatorEditText.OnSelectionChangedListener { selStart, selEnd ->
            model.inputSelection = Pair(selStart, selEnd)
        }

        binding.swapButton.setOnClickListener {
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
                    model.setExpression(items[selectedItemPosition].displayValue)
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
        val binding = mBinding ?: return true

        val convertToItem = items[binding.spinnerTo.selectedItemPosition];

        return model.copy(
                requireContext(), binding.valueConverted.text,
                getString(convertToItem.shortNameResourceId), getString(convertToItem.nameResourceId),
                copyMode
        )
    }

    private fun setSimpleViewButtonsClickListener() {
        val binding = mBinding ?: return

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
        val from = items[position]

        val inputValue = calculate(value) ?: emptyValueInterpretation.toDouble()

        from.setValue(inputValue)

        for (i in items.indices) {
            if (i != position) {
                items[i].convert(from)
            }
        }

        val binding = mBinding ?: return
        binding.valueConverted.textValue = items[binding.spinnerTo.selectedItemPosition].displayValue
    }

    private val UnitConverterItem<Double>.displayValue
        get() = model.calculations.format(currentValue)

}