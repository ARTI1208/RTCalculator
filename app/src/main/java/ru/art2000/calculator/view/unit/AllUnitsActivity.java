package ru.art2000.calculator.view.unit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.ActivityAllUnitsBinding;
import ru.art2000.calculator.view_model.currency.CurrenciesSettingsModel;
import ru.art2000.calculator.view_model.unit.UnitConverterDependencies;
import ru.art2000.calculator.view_model.unit.UnitConverterModel;
import ru.art2000.extensions.activities.AutoThemeActivity;

public class AllUnitsActivity extends AutoThemeActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAllUnitsBinding binding = ActivityAllUnitsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UnitConverterModel model = new ViewModelProvider(this).get(UnitConverterModel.class);

        String category = getIntent().getStringExtra("category");

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String title = getResources().getStringArray(R.array.unit_converter_categories)
                [UnitConverterDependencies.getCategoryInt(category)];
        getSupportActionBar().setTitle(title);

        int pos = getIntent().getIntExtra("highlightPosition", 0);

        RecyclerView list = binding.allUnitsList;
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);
        UnitListAdapter adapter = new UnitListAdapter(this, this,
                UnitConverterDependencies.getCategoryItems(category), model, pos);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
