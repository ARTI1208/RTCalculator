package ru.art2000.calculator.unit.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.unit.model.UnitCategory
import ru.art2000.calculator.common.view.MainScreenFragment
import ru.art2000.calculator.unit.R
import ru.art2000.calculator.unit.databinding.UnitLayoutBinding
import ru.art2000.calculator.unit.view.BaseUnitPageFragment.Companion.newInstance
import ru.art2000.extensions.views.createOnTabSelectedListener
import ru.art2000.calculator.unit.preferences.UnitPreferenceHelper
import ru.art2000.calculator.unit.model.ViewType
import ru.art2000.extensions.views.MyFragmentStateAdapter
import javax.inject.Inject

@AndroidEntryPoint
internal class UnitConverterFragment : MainScreenFragment(R.layout.unit_layout) {

    @Inject
    internal lateinit var prefsHelper: UnitPreferenceHelper

    private var pager2Mediator: ru.art2000.extensions.views.MyTabLayoutMediator? = null
    private val binding by viewBinding(UnitLayoutBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateAdapter()
        prefsHelper.setOnViewTypeChangedListener {
            updateAdapter(it, true)
        }
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

        val pager2Adapter = UnitPagerAdapter(ViewType.of(viewType))
        if (clear) pager2Adapter.clearState()

        binding.pager2.adapter = pager2Adapter

        pager2Mediator?.detach()

        binding.pager2.setCurrentItem(previousItem, false)

        val titles = resources.getStringArray(R.array.unit_converter_categories)

        binding.tabs.clearOnTabSelectedListeners()
        binding.tabs.addOnTabSelectedListener(pager2Adapter.createOnTabSelectedListener())
        pager2Mediator = ru.art2000.extensions.views.MyTabLayoutMediator(
            binding.tabs,
            binding.pager2
        ) { tab, position ->
            tab.text = titles[position]
        }.apply {
            attach()
        }
    }

    private inner class UnitPagerAdapter(
        type: ViewType,
    ) : MyFragmentStateAdapter<BaseUnitPageFragment>(this@UnitConverterFragment) {

        var viewType = type
            @SuppressLint("NotifyDataSetChanged")
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        private val shift: Long
            get() = (Int.MAX_VALUE + 1L) shl viewType.ordinal

        override fun createFragment(position: Int): BaseUnitPageFragment {
            return newInstance(UnitCategory.ofOrdinal(position), viewType)
        }

        override fun getItemCount(): Int {
            return UnitCategory.entries.size
        }

        override fun getItemId(position: Int): Long {
            return shift + position
        }

        override fun containsItem(itemId: Long): Boolean {
            return itemId and shift == shift
        }
    }
}