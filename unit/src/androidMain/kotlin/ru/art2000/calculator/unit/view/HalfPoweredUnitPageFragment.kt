package ru.art2000.calculator.unit.view

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.unit.R
import ru.art2000.calculator.unit.databinding.UnitFragHalfBinding
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.extensions.views.OrientationManger
import ru.art2000.extensions.views.SimpleTextWatcher
import ru.art2000.extensions.views.addOrientationItemDecoration
import ru.art2000.extensions.views.isLandscape

@AndroidEntryPoint
internal class HalfPoweredUnitPageFragment : BaseUnitPageFragment(R.layout.unit_frag_half) {

    private val binding by viewBinding(UnitFragHalfBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val unitListAdapter = UnitListAdapter(
            requireContext(), viewLifecycleOwner,
            converterFunctions, items, model::copy, false,
        )

        binding.unitRv.apply {
            adapter = unitListAdapter
            layoutManager = OrientationManger(requireContext())
            addOrientationItemDecoration()

            registerForContextMenu(this)
        }

        val inputText = model.expression
        binding.hpuvEt.setText(inputText)
        binding.hpuvEt.setSelection(inputText.length)
        binding.hpuvEt.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                unitListAdapter.setValueForPosition(binding.hpuvSpinner.selectedItemPosition)
            }
        })

        binding.hpuvSpinner.adapter = createSpinnerAdapter()

        binding.hpuvSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                unitListAdapter.setValueForPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.hpuvSpinner.setSelection(
            model.converterFunctions.getInt(CONVERT_FROM_KEY, 0)
        )
    }

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) listOf(binding.unitRv) else emptyList()

    override fun onShown(previousReplaceable: IReplaceableFragment?) {

        while (!binding.hpuvEt.isFocused) {
            binding.hpuvEt.requestFocus()
        }

        binding.hpuvEt.postDelayed(binding.hpuvEt::requestFocus, 100L)
    }

    override fun onReselected() {
        binding.unitRv.smoothScrollToPosition(0)
    }

    private fun UnitListAdapter.setValueForPosition(position: Int) {
        setValue(position, binding.hpuvEt.text)
    }
}