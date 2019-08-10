package ru.art2000.calculator.unit_converter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.art2000.calculator.R;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.helpers.PrefsHelper;

public class AllUnitsActivity extends DayNightActivity {

    double value;
    int pos;
    String[] dims;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        value = getIntent().getDoubleExtra("value", 1);
        pos = getIntent().getIntExtra("pos", 0);
        category = getIntent().getStringExtra("category");
        dims = getIntent().getStringArrayExtra("dims");
        String title = getResources().getStringArray(R.array.unit_converter_categories)
                [Formulas.getCategoryInt(category)];
        getSupportActionBar().setTitle(title);
        RecyclerView list = findViewById(R.id.all_units_list);
        list.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);
        UnitListAdapter adapter = new UnitListAdapter(mContext, dims,
                Formulas.getCategoryInt(category), pos, value);
        list.setAdapter(adapter);
    }
}
