package ru.art2000.calculator.unit.view

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.unit.R
import ru.art2000.calculator.unit.databinding.UnitFragBinding
import ru.art2000.extensions.views.OrientationManger
import ru.art2000.extensions.views.addOrientationItemDecoration
import ru.art2000.extensions.views.isLandscape

@AndroidEntryPoint
internal class PowerfulUnitPageFragment : BaseUnitPageFragment(R.layout.unit_frag) {

    private val binding by viewBinding(UnitFragBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.unitRv.apply {
            layoutManager = OrientationManger(requireContext())
            addOrientationItemDecoration()

            adapter = UnitListAdapter(
                requireContext(), viewLifecycleOwner,
                converterFunctions, items, model::copy, true,
            )
        }
    }

    override fun onReselected() {
        binding.unitRv.smoothScrollToPosition(0)
    }

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) listOf(binding.unitRv) else emptyList()
}