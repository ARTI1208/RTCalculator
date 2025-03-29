package ru.art2000.calculator.unit.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.annotation.CallSuper
import androidx.core.os.bundleOf
import androidx.viewbinding.ViewBinding
import ru.art2000.calculator.unit.model.UnitCategory
import ru.art2000.calculator.unit.model.ViewType
import ru.art2000.calculator.unit.vm.UnitConverterModel
import ru.art2000.extensions.activities.IEdgeToEdgeFragment
import ru.art2000.extensions.activities.getEnum
import ru.art2000.extensions.arch.assistedViewModel
import ru.art2000.extensions.fragments.CommonReplaceableFragment
import javax.inject.Inject

internal abstract class BaseUnitPageFragment<VB : ViewBinding> : CommonReplaceableFragment(),
    IEdgeToEdgeFragment {

    companion object {

        const val CONVERT_FROM_KEY = "from"

        private const val categoryKey = "category"

        @JvmStatic
        fun newInstance(category: UnitCategory, viewType: ViewType): BaseUnitPageFragment<*> {
            return when (viewType) {
                ViewType.SIMPLE -> SimpleUnitPageFragment()
                ViewType.POWERFUL -> PowerfulUnitPageFragment()
                ViewType.ERGONOMIC -> HalfPoweredUnitPageFragment()
            }.passCategoryToFragment(category)
        }

        private fun BaseUnitPageFragment<*>.passCategoryToFragment(category: UnitCategory): BaseUnitPageFragment<*> {
            arguments = bundleOf(categoryKey to category)
            return this
        }
    }

    protected val category by lazy {
        requireArguments().getEnum<UnitCategory>(categoryKey)!!
    }

    protected val binding: VB
        get() = mBinding!!

    private var mBinding: VB? = null

    @Inject
    lateinit var viewModelFactory: UnitConverterModel.Factory

    protected val model by assistedViewModel { viewModelFactory.create(category) }

    abstract fun inflate(inflater: LayoutInflater, container: ViewGroup?): VB

    protected open fun setup() {}

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        model.updateLocaleSpecific()

        return (mBinding ?: inflate(inflater, container).also {
            mBinding = it
            setup()
        }).root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    protected val converterFunctions by lazy { model.converterFunctions }

    protected val items by lazy { model.converterNames }

    protected fun createSpinnerAdapter(): SpinnerAdapter {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items.map { getString(it.shortName) }
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }
}