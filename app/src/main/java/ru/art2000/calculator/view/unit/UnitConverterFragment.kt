package ru.art2000.calculator.view.unit

import android.content.SharedPreferences
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitLayoutBinding
import ru.art2000.calculator.view.MainScreenFragment
import ru.art2000.calculator.view.settings.PreferenceKeys
import ru.art2000.calculator.view.unit.BaseUnitPageFragment.Companion.newInstance
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.helpers.PrefsHelper
import ru.art2000.helpers.getLocalizedArray
import java.lang.ref.WeakReference
import java.util.*

internal class UnitConverterFragment : MainScreenFragment() {

    private var pageChangeCallback2: OnPageChangeCallback? = null
    private var pager2Mediator: TabLayoutMediator? = null
    private val binding by viewBinding<UnitLayoutBinding>(CreateMethod.INFLATE)

    private var currentViewType: String = PrefsHelper.unitViewType

    private val preferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == PreferenceKeys.KEY_UNIT_VIEW) {
                updateAdapter(prefs.getString(key, "simple")!!)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        updateAdapter()

        currentViewType = PrefsHelper.unitViewType
        PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(preferenceListener)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(preferenceListener)
        pageChangeCallback2 = null
        pager2Mediator = null
    }

    private fun updateAdapter(viewType: String = PrefsHelper.unitViewType) {

        val pager2Adapter = UnitPagerAdapter(viewType)
        binding.pager2.adapter = pager2Adapter

        pageChangeCallback2?.also {
            binding.pager2.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback2 = object : OnPageChangeCallback() {
            private var isFirstRun = true
            override fun onPageSelected(position: Int) {
                if (!isFirstRun) {
                    pager2Adapter.getFragment(position).onReplace(null)
                }
                isFirstRun = false
            }
        }
        binding.pager2.registerOnPageChangeCallback(pageChangeCallback2!!)
        pager2Mediator?.detach()
        pager2Mediator = TabLayoutMediator(binding.tabs, binding.pager2) { tab, position ->
            tab.text = pager2Adapter.getPageTitle(position)
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

    override fun onShown(previousReplaceable: IReplaceableFragment?) {
        val adapter = binding.pager2.adapter as? UnitPagerAdapter ?: return
        adapter.getFragment(binding.pager2.currentItem).onShown(null)
    }

    private inner class UnitPagerAdapter(
        private val viewType: String,
    ) : FragmentStateAdapter(this@UnitConverterFragment) {

        private val categoriesNames = resources.getStringArray(R.array.unit_converter_categories)

        private val categoriesEnglish = requireContext().getLocalizedArray(
            Locale.ENGLISH,
            R.array.unit_converter_categories
        )

        private val sparseFragments =
            SparseArray<WeakReference<BaseUnitPageFragment<*>>>(categoriesNames.size)

        fun getPageTitle(position: Int): CharSequence {
            return categoriesNames[position]
        }

        fun getFragment(position: Int) = createFragment(position)

        override fun createFragment(position: Int): BaseUnitPageFragment<*> {

            sparseFragments[position]?.get()?.also {
                return it
            }

            return newInstance(
                categoriesEnglish[position].lowercase(Locale.ENGLISH),
                viewType
            ).also {
                sparseFragments[position] = WeakReference(it)
            }
        }

        override fun getItemCount(): Int {
            return categoriesNames.size
        }
    }
}