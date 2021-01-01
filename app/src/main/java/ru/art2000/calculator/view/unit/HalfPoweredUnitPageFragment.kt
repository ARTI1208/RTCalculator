package ru.art2000.calculator.view.unit

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.art2000.calculator.databinding.UnitFragHalfBinding
import ru.art2000.calculator.view_model.calculator.CalculationClass
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.extensions.views.SimpleTextWatcher

class HalfPoweredUnitPageFragment : BaseUnitPageFragment<UnitFragHalfBinding>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragHalfBinding {
        return UnitFragHalfBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        registerForContextMenu(binding.unitRv)

        val adapter = UnitListAdapter(requireContext(), viewLifecycleOwner, items, false)

        binding.unitRv.adapter = adapter
        binding.unitRv.layoutManager = LinearLayoutManager(requireContext())
        binding.unitRv.addItemDecoration(
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

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
        val value = CalculationClass.calculate(binding.hpuvEt.text.toString()) ?: 1.0
        setValue(position, value)
    }
}