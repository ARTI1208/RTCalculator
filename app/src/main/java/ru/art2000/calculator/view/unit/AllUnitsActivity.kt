package ru.art2000.calculator.view.unit

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityAllUnitsBinding
import ru.art2000.calculator.model.unit.UnitCategory
import ru.art2000.calculator.view.AppActivity
import ru.art2000.calculator.view_model.unit.UnitConverterModel
import ru.art2000.extensions.activities.getEnumExtra
import ru.art2000.extensions.arch.assistedViewModel
import ru.art2000.extensions.views.OrientationManger
import ru.art2000.extensions.views.addOrientationItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class AllUnitsActivity : AppActivity() {

    @Inject
    lateinit var viewModelFactory: UnitConverterModel.Factory

    private val category by lazy {
        intent.getEnumExtra<UnitCategory>("category")!!
    }

    private val model by assistedViewModel { viewModelFactory.create(category) }
    private val binding by viewBinding<ActivityAllUnitsBinding>(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val pos = intent.getIntExtra("highlightPosition", 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title =
            resources.getStringArray(R.array.unit_converter_categories)[category.ordinal]
        supportActionBar?.title = title

        val unitListAdapter = UnitListAdapter(
            this, this,
            model.converterFunctions, model::copy, pos,
        )

        binding.allUnitsList.apply {
            layoutManager = OrientationManger(this@AllUnitsActivity)
            addOrientationItemDecoration()
            adapter = unitListAdapter
        }
    }

    override val topViews: List<View>
        get() = listOf(binding.root)

    override val bottomViews: List<View>
        get() = listOf(binding.allUnitsList)

    override val leftViews: List<View>
        get() = listOf(binding.root)

    override val rightViews: List<View>
        get() = listOf(binding.root)

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}