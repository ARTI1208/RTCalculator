package ru.art2000.calculator.view.unit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.databinding.UnitFragBinding
import ru.art2000.extensions.views.OrientationManger
import ru.art2000.extensions.views.addOrientationItemDecoration
import ru.art2000.extensions.views.isLandscape

@AndroidEntryPoint
class PowerfulUnitPageFragment : BaseUnitPageFragment<UnitFragBinding>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): UnitFragBinding {
        return UnitFragBinding.inflate(inflater, container, false)
    }

    override fun setup() {

        binding.unitRv.apply {
            layoutManager = OrientationManger(requireContext())
            addOrientationItemDecoration()

            adapter = UnitListAdapter(
                requireContext(), viewLifecycleOwner,
                converterFunctions, model::copy, true,
            )
        }
    }

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) listOf(binding.unitRv) else emptyList()
}