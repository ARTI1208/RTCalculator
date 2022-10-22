package ru.art2000.calculator.view.unit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityAllUnitsBinding
import ru.art2000.calculator.model.unit.UnitCategory
import ru.art2000.calculator.view_model.unit.UnitConverterModel
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.activities.getEnumExtra

class AllUnitsActivity : AutoThemeActivity() {

    val model by viewModels<UnitConverterModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAllUnitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getEnumExtra<UnitCategory>("category")!!
        val pos = intent.getIntExtra("highlightPosition", 0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title =
            resources.getStringArray(R.array.unit_converter_categories)[category.ordinal]
        supportActionBar?.title = title

        val list = binding.allUnitsList
        list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        list.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.VERTICAL
        }

        val adapter = UnitListAdapter(
            this, this,
            model.getConverterFunctions(category), model::copy, pos,
        )
        list.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}