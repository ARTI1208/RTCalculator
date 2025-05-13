package ru.art2000.calculator.unit.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.unit.R
import ru.art2000.calculator.unit.databinding.ActivityAllUnitsBinding
import ru.art2000.calculator.unit.vm.UnitConverterModel
import ru.art2000.extensions.views.OrientationManger
import ru.art2000.extensions.views.addOrientationItemDecoration

@AndroidEntryPoint
internal class AllUnitsActivity : AppActivity(R.layout.activity_all_units) {

    private val model by viewModels<UnitConverterModel>()
    private val binding by viewBinding(ActivityAllUnitsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pos = intent.getIntExtra("highlightPosition", 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title =
            resources.getStringArray(R.array.unit_converter_categories)[model.category.ordinal]
        supportActionBar?.title = title

        val unitListAdapter = UnitListAdapter(
            this, this,
            model.converterFunctions, model.converterNames, model::copy, pos,
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