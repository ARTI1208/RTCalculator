package ru.art2000.calculator.view.unit

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.databinding.UnitFragHalfBinding
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.extensions.views.SimpleTextWatcher

@AndroidEntryPoint
class HalfPoweredUnitPageFragment : BaseUnitPageFragment<UnitFragHalfBinding>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragHalfBinding {
        return UnitFragHalfBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        registerForContextMenu(binding.unitRv)

        val adapter = UnitListAdapter(
            requireContext(), viewLifecycleOwner,
            converterFunctions, model::copy, false,
        )

        binding.unitRv.adapter = adapter
        binding.unitRv.layoutManager = LinearLayoutManager(requireContext())
        binding.unitRv.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        val inputText = model.expression
        binding.hpuvEt.setText(inputText)
        binding.hpuvEt.setSelection(inputText.length)
        binding.hpuvEt.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                adapter.setValueForPosition(binding.hpuvSpinner.selectedItemPosition)
            }
        })

        binding.hpuvSpinner.adapter = createSpinnerAdapter()

        binding.hpuvSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter.setValueForPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onShown(previousReplaceable: IReplaceableFragment?) {

        while (!binding.hpuvEt.isFocused) {
            binding.hpuvEt.requestFocus()
        }

        binding.hpuvEt.postDelayed(binding.hpuvEt::requestFocus, 100L)
    }

    private fun UnitListAdapter.setValueForPosition(position: Int) {
        setValue(position, binding.hpuvEt.text)
    }
}