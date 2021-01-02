package ru.art2000.calculator.view.unit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitFragSimpleBinding
import ru.art2000.calculator.model.unit.UnitConverterItem
import ru.art2000.calculator.view_model.calculator.CalculationClass
import ru.art2000.extensions.views.CalculatorEditText
import ru.art2000.extensions.views.SimpleTextWatcher
import ru.art2000.extensions.views.postFullScrollRight
import ru.art2000.extensions.views.textValue
import ru.art2000.helpers.GeneralHelper

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

        binding.valueOriginal.setText(model.expression) // required to correctly place selection

        setSimpleViewButtonsClickListener()

        binding.valueOriginal.addTextChangedListener(object : SimpleTextWatcher() {

            override fun afterTextChanged(s: Editable) {
                model.liveExpression.value = s.toString()
            }
        })

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

        binding.valueConverted.addTextChangedListener(object : SimpleTextWatcher() {

            override fun afterTextChanged(s: Editable) {
                binding.convertedHsv.postFullScrollRight()
            }
        })

        binding.swapButton.setOnClickListener {
            binding.spinnerFrom.setSelection(binding.spinnerTo.selectedItemPosition)
        }

        val spinnerAdapter = createSpinnerAdapter()
        binding.spinnerFrom.adapter = spinnerAdapter
        binding.spinnerTo.adapter = spinnerAdapter

        binding.spinnerFrom.setSelection(spinnerFromPosition)
        binding.spinnerTo.setSelection(spinnerToPosition)

        binding.spinnerFrom.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,
                                        itemSelected: View?,
                                        selectedItemPosition: Int,
                                        selectedId: Long) {
                binding.originalDimensionHint.text = spinnerAdapter.getItem(selectedItemPosition)
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
            override fun onItemSelected(parent: AdapterView<*>?,
                                        itemSelected: View?, selectedItemPosition: Int, selectedId: Long) {
                binding.convertedDimensionHint.text = spinnerAdapter.getItem(selectedItemPosition)
                if (selectedItemPosition == spinnerFromPosition) binding.spinnerFrom.setSelection(spinnerToPosition)
                spinnerToPosition = selectedItemPosition
                updateResult(binding.spinnerFrom.selectedItemPosition, binding.valueOriginal.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        registerForContextMenu(binding.valueConverted)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy).setOnMenuItemClickListener {
            onContextItemClick(it, v)
        }

    }

    private fun onContextItemClick(item: MenuItem, view: View): Boolean {
        val cmg = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                ?: return true

        when (item.itemId) {
            MENU_ITEM_COPY -> {
                val copiedText = if (view.id == binding.valueOriginal.id)
                    binding.valueOriginal.text.toString() + " " + binding.originalDimensionHint.text
                else
                    binding.valueConverted.text.toString() + " " + binding.convertedDimensionHint.text

                cmg.setPrimaryClip(ClipData.newPlainText("unitConvertResult", copiedText))

                val toastText = requireContext().getString(R.string.copied) + " " + copiedText
                Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
            }
        }

        return true
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
        val from: UnitConverterItem = items[position]

        val inputValue = CalculationClass.calculate(value) ?: 1.0

        from.setValue(inputValue)

        for (i in items.indices) {
            if (i != position) {
                items[i].convert(from)
            }
        }

        binding.valueConverted.textValue = items[binding.spinnerTo.selectedItemPosition].displayValue
    }

    private val UnitConverterItem.displayValue
        get() = GeneralHelper.resultNumberFormat.format(this.currentValue)

}