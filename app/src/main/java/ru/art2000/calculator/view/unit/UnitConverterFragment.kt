package ru.art2000.calculator.view.unit

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.UnitLayoutBinding
import ru.art2000.calculator.view.MainScreenFragment
import ru.art2000.calculator.view.settings.PreferenceKeys
import ru.art2000.calculator.view.unit.BaseUnitPageFragment.Companion.newInstance
import ru.art2000.extensions.fragments.IReplaceableFragment
import ru.art2000.helpers.PrefsHelper
import ru.art2000.helpers.getLocalizedArray
import java.util.*

internal class UnitConverterFragment : MainScreenFragment() {

    private var pageChangeCallback2: OnPageChangeCallback? = null
    private var pager2Mediator: TabLayoutMediator? = null
    private var binding: UnitLayoutBinding? = null

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
        if (binding == null) {
            val viewBinding = UnitLayoutBinding.inflate(inflater)
            binding = viewBinding
            updateAdapter()

            currentViewType = PrefsHelper.unitViewType
            PreferenceManager
                    .getDefaultSharedPreferences(requireContext())
                    .registerOnSharedPreferenceChangeListener(preferenceListener)
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .unregisterOnSharedPreferenceChangeListener(preferenceListener)
        pageChangeCallback2 = null
        pager2Mediator = null
        binding = null
    }

    private fun updateAdapter(viewType: String = PrefsHelper.unitViewType) {
        val nonNullBinding = binding ?: return

        val pager2Adapter = UnitPagerAdapter(viewType)
        nonNullBinding.pager2.adapter = pager2Adapter

        pageChangeCallback2?.also {
            nonNullBinding.pager2.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback2 = object : OnPageChangeCallback() {
            private var isFirstRun = true
            override fun onPageSelected(position: Int) {
                if (!isFirstRun) {
                    pager2Adapter.fragments[position].onReplace(null)
                }
                isFirstRun = false
            }
        }
        nonNullBinding.pager2.registerOnPageChangeCallback(pageChangeCallback2!!)
        pager2Mediator?.detach()
        pager2Mediator = TabLayoutMediator(
                nonNullBinding.tabs,
                nonNullBinding.pager2
        ) { tab, position -> tab.text = pager2Adapter.getPageTitle(position) }.apply {
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
        val nonNullBinding = binding ?: return
        val adapter = nonNullBinding.pager2.adapter as? UnitPagerAdapter ?: return
        adapter.fragments[nonNullBinding.pager2.currentItem].onShown(null)
    }

    private inner class UnitPagerAdapter(
            viewType: String,
    ) : FragmentStateAdapter(this@UnitConverterFragment) {

        private val categoriesNames = resources.getStringArray(R.array.unit_converter_categories)

        val fragments = kotlin.run {
            val categoriesEnglish = requireContext().getLocalizedArray(Locale.ENGLISH, R.array.unit_converter_categories)
            Array(categoriesNames.size) { newInstance(categoriesEnglish[it].lowercase(Locale.ENGLISH), viewType) }
        }

        fun getPageTitle(position: Int): CharSequence {
            return categoriesNames[position]
        }

        override fun createFragment(position: Int): BaseUnitPageFragment<*> {
            return fragments[position]
        }

        override fun getItemCount(): Int {
            return categoriesNames.size
        }
    }
}