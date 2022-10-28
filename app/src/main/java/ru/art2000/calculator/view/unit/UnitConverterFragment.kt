package ru.art2000.calculator.view.unit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitLayoutBinding
import ru.art2000.calculator.model.unit.UnitCategory
import ru.art2000.calculator.view.MainScreenFragment
import ru.art2000.calculator.view.unit.BaseUnitPageFragment.Companion.newInstance
import ru.art2000.extensions.views.MyFragmentStateAdapter
import ru.art2000.extensions.views.MyTabLayoutMediator
import ru.art2000.extensions.views.createOnTabSelectedListener
import ru.art2000.helpers.UnitPreferenceHelper
import javax.inject.Inject

@AndroidEntryPoint
internal class UnitConverterFragment : MainScreenFragment() {

    @Inject
    lateinit var prefsHelper: UnitPreferenceHelper

    private var pager2Mediator: MyTabLayoutMediator? = null
    private val binding by viewBinding<UnitLayoutBinding>(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        updateAdapter()
        prefsHelper.setOnViewTypeChangedListener {
            updateAdapter(it, true)
        }

        return binding.root
    }

    override val topViews: List<View>
        get() = listOf(binding.root)

    override fun onDestroyView() {
        super.onDestroyView()
        prefsHelper.setOnViewTypeChangedListener(null)
        pager2Mediator = null
    }

    private fun updateAdapter(viewType: String = prefsHelper.unitViewType, clear: Boolean = false) {

        val previousItem = binding.pager2.currentItem

        val pager2Adapter = UnitPagerAdapter(viewType)
        if (clear) pager2Adapter.clearState()

        binding.pager2.adapter = pager2Adapter

        pager2Mediator?.detach()

        binding.pager2.setCurrentItem(previousItem, false)

        val titles = resources.getStringArray(R.array.unit_converter_categories)

        binding.tabs.clearOnTabSelectedListeners()
        binding.tabs.addOnTabSelectedListener(pager2Adapter.createOnTabSelectedListener())
        pager2Mediator = MyTabLayoutMediator(binding.tabs, binding.pager2) { tab, position ->
            tab.text = titles[position]
        }.apply {
            attach()
        }
    }

    private inner class UnitPagerAdapter(
        private val viewType: String,
    ) : MyFragmentStateAdapter<BaseUnitPageFragment<*>>(this@UnitConverterFragment) {

        override fun createFragment(position: Int): BaseUnitPageFragment<*> {
            return newInstance(UnitCategory.ofOrdinal(position), viewType)
        }

        override fun getItemCount(): Int {
            return UnitCategory.count
        }
    }
}