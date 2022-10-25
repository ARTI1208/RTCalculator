package ru.art2000.calculator.view.unit

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitLayoutBinding
import ru.art2000.calculator.model.unit.UnitCategory
import ru.art2000.calculator.view.MainScreenFragment
import ru.art2000.calculator.view.unit.BaseUnitPageFragment.Companion.newInstance
import ru.art2000.extensions.views.MyTabLayoutMediator
import ru.art2000.helpers.UnitPreferenceHelper
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class UnitConverterFragment : MainScreenFragment() {

    @Inject
    lateinit var prefsHelper: UnitPreferenceHelper

    private var pageChangeCallback2: OnPageChangeCallback? = null
    private var pager2Mediator: MyTabLayoutMediator? = null
    private val binding by viewBinding<UnitLayoutBinding>(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        updateAdapter()
        prefsHelper.setOnViewTypeChangedListener(::updateAdapter)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        prefsHelper.setOnViewTypeChangedListener(null)
        pageChangeCallback2 = null
        pager2Mediator = null
    }

    private fun updateAdapter(viewType: String = prefsHelper.unitViewType) {

        val pager2Adapter = UnitPagerAdapter(viewType)
        binding.pager2.adapter = pager2Adapter

        pageChangeCallback2?.also {
            binding.pager2.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback2 = object : OnPageChangeCallback() {

            var previousPosition = -1

            val previousFragment: BaseUnitPageFragment<*>?
                get() = if (previousPosition in 0 until pager2Adapter.itemCount) {
                    pager2Adapter.getFragment(previousPosition)
                } else null

            override fun onPageSelected(position: Int) {
                pager2Adapter.getFragment(position).onReplace(previousFragment)
                previousPosition = position
            }
        }
        binding.pager2.registerOnPageChangeCallback(pageChangeCallback2!!)
        pager2Mediator?.detach()

        val titles = resources.getStringArray(R.array.unit_converter_categories)

        pager2Mediator = MyTabLayoutMediator(binding.tabs, binding.pager2) { tab, position ->
            tab.text = titles[position]
        }.apply {
            attach()
        }
    }

    override fun getTitle(): Int {
        return R.string.title_unit
    }

    override fun getIcon(): Int {
        return R.drawable.ic_unit
    }

    override fun getReplaceableId(): Int {
        return R.id.navigation_unit
    }

    private inner class UnitPagerAdapter(
        private val viewType: String,
    ) : FragmentStateAdapter(this@UnitConverterFragment) {

        private val sparseFragments =
            SparseArray<WeakReference<BaseUnitPageFragment<*>>>(UnitCategory.count)

        fun getFragment(position: Int) = createFragment(position)

        override fun createFragment(position: Int): BaseUnitPageFragment<*> {

            sparseFragments[position]?.get()?.also {
                return it
            }

            return newInstance(
                UnitCategory.ofOrdinal(position), viewType
            ).also {
                sparseFragments[position] = WeakReference(it)
            }
        }

        override fun getItemCount(): Int {
            return UnitCategory.count
        }
    }
}