package ru.art2000.calculator.view.unit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import ru.art2000.calculator.view_model.unit.UnitConverterDependencies
import ru.art2000.calculator.view_model.unit.UnitConverterModel
import ru.art2000.extensions.fragments.CommonReplaceableFragment

abstract class BaseUnitPageFragment<VB : ViewBinding> : CommonReplaceableFragment() {

    companion object {

        private const val defaultCategory = "area"

        private const val categoryKey = "category"

        @JvmStatic
        fun newInstance(category: String, viewType: String): BaseUnitPageFragment<*> {
            return when (viewType) {
                "simple" -> SimpleUnitPageFragment()
                "powerful" -> PowerfulUnitPageFragment()
                else -> HalfPoweredUnitPageFragment()
            }.passCategoryToFragment(category)
        }

        private fun BaseUnitPageFragment<*>.passCategoryToFragment(category: String): BaseUnitPageFragment<*> {
            arguments = bundleOf(categoryKey to category)
            return this
        }
    }

    protected val category: String by lazy {
        arguments?.getString("category", defaultCategory) ?: defaultCategory
    }

    protected var mBinding: VB? = null
        private set

    protected val model: UnitConverterModel by viewModels()

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    protected open fun setup() {}

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return (mBinding ?: inflate(inflater, container).also {
            mBinding = it
            setup()
        }).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    protected val items by lazy { UnitConverterDependencies.getCategoryItems(category) }

    protected fun createSpinnerAdapter(): SpinnerAdapter {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items.map { getString(it.nameResourceId) }
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }
}