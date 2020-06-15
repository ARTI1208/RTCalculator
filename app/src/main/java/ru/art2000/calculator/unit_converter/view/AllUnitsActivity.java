package ru.art2000.calculator.unit_converter.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.unit_converter.view_model.UnitConverterDependencies;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.helpers.PrefsHelper;

public class AllUnitsActivity extends DayNightActivity {

    int pos;
    String category;
    Context mContext;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PrefsHelper.getAppTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_units);
        mContext = this;
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        pos = getIntent().getIntExtra("pos", 0);
        category = getIntent().getStringExtra("category");
        String title = getResources().getStringArray(R.array.unit_converter_categories)
                [UnitConverterDependencies.getCategoryInt(category)];
        getSupportActionBar().setTitle(title);
        RecyclerView list = findViewById(R.id.all_units_list);
        list.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);
        UnitListAdapter adapter = new UnitListAdapter(mContext,
                UnitConverterDependencies.getCategoryItems(category), pos);
        list.setAdapter(adapter);
    }
}
