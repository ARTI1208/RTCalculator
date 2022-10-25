package ru.art2000.calculator.view.unit

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityAllUnitsBinding
import ru.art2000.calculator.model.unit.UnitCategory
import ru.art2000.calculator.view_model.unit.UnitConverterModel
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.activities.getEnumExtra
import ru.art2000.extensions.arch.assistedViewModel
import ru.art2000.extensions.views.OrientationManger
import ru.art2000.extensions.views.addOrientationItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class AllUnitsActivity : AutoThemeActivity() {

    @Inject
    lateinit var viewModelFactory: UnitConverterModel.Factory

    private val category by lazy {
        intent.getEnumExtra<UnitCategory>("category")!!
    }

    private val model by assistedViewModel { viewModelFactory.create(category) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAllUnitsBinding.inflate(layoutInflater)
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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}