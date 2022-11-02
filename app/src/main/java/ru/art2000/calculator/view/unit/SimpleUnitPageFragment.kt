package ru.art2000.calculator.view.unit

import android.content.Intent
import android.text.Editable
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.SimpleKeyboardBinding
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

        private const val CONVERT_TO_KEY = "to"

    }

    private var spinnerFromPosition = 0
        set(value) {
            model.converterFunctions.storeInt(CONVERT_FROM_KEY, value)
            field = value
        }
    private var spinnerToPosition = 1
        set(value) {
            model.converterFunctions.storeInt(CONVERT_TO_KEY, value)
            field = value
        }

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragSimpleBinding {
        return UnitFragSimpleBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        val keyboardBinding = binding.keyboard

        binding.valueOriginal.setText(model.expression) // required to correctly place selection
        binding.valueOriginal.hint = converterFunctions.defaultValueString

        keyboardBinding.buttonDot.text = model.decimalSeparator.toString()

        keyboardBinding.buttonExtra.apply {
            setImageResource(R.drawable.ic_list_all)
            background = keyboardBinding.buttonDot.background?.constantState?.newDrawable()
            ImageViewCompat.setImageTintList(this, keyboardBinding.buttonDot.textColors)
        }

        setSimpleViewButtonsClickListener(keyboardBinding, binding.spinnerFrom)

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

                updateResult(expression)
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

        binding.valueOriginal.onSelectionChangedListener =
            CustomInputEditText.OnSelectionChangedListener { selStart, selEnd ->
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

        binding.spinnerFrom.setSelection(
            model.converterFunctions.getInt(CONVERT_FROM_KEY, 0)
        )
        binding.spinnerTo.setSelection(
            model.converterFunctions.getInt(CONVERT_TO_KEY, 1)
        )

        binding.spinnerFrom.isSaveEnabled = false
        binding.spinnerFrom.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                itemSelected: View?,
                selectedItemPosition: Int,
                selectedId: Long
            ) {
                binding.originalDimensionHint.text =
                    spinnerAdapter.getItem(selectedItemPosition)?.toString()
                if (selectedItemPosition == spinnerToPosition) {
                    model.setExpression(converterFunctions.displayValue(selectedItemPosition))
                    binding.spinnerTo.setSelection(spinnerFromPosition)
                }
                spinnerFromPosition = selectedItemPosition
                updateResult()
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
                binding.convertedDimensionHint.text =
                    spinnerAdapter.getItem(selectedItemPosition)?.toString()
                if (selectedItemPosition == spinnerFromPosition) {
                    binding.spinnerFrom.setSelection(spinnerToPosition)
                }
                spinnerToPosition = selectedItemPosition
                updateResult()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        registerForContextMenu(binding.valueConverted)
    }

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) listOf(binding.root) else emptyList()

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy_value)
            .setOnMenuItemClickListener {
                copy(CopyMode.VALUE_ONLY)
            }

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy_with_short_name)
            .setOnMenuItemClickListener {
                copy(CopyMode.VALUE_AND_SHORT_NAME)
            }

        menu.add(0, MENU_ITEM_COPY, 0, R.string.context_menu_copy_with_full_name)
            .setOnMenuItemClickListener {
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

    private fun setSimpleViewButtonsClickListener(
        binding: SimpleKeyboardBinding,
        spinnerFrom: Spinner,
    ) {

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

        binding.buttonExtra.setOnClickListener {
            val intent = Intent(requireContext(), AllUnitsActivity::class.java)
            intent.putExtra("highlightPosition", spinnerFrom.selectedItemPosition)
            intent.putExtra("category", category)
            requireContext().startActivity(intent)
        }
    }

    private fun updateResult(value: String = binding.valueOriginal.text.toString()) {
        val fromPosition = binding.spinnerFrom.selectedItemPosition
        val toPosition = binding.spinnerTo.selectedItemPosition

        converterFunctions.setValue(fromPosition, value, object : ConverterFunctions.ValueCallback {
            override fun shouldSkip(i: Int) = i == fromPosition
        })

        binding.valueConverted.textValue = converterFunctions.displayValue(toPosition)
    }

}