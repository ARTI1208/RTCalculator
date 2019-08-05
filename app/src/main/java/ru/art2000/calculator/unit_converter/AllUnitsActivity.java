package ru.art2000.calculator.unit_converter;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.art2000.calculator.R;
import ru.art2000.calculator.settings.PrefsHelper;
import ru.art2000.extensions.DayNightActivity;

public class AllUnitsActivity extends DayNightActivity {

    int value;
    int pos;
    String[] dims;
    String category;

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
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        value = getIntent().getIntExtra("value", 1);
        pos = getIntent().getIntExtra("pos", 0);
        category = getIntent().getStringExtra("category");
        dims = getIntent().getStringArrayExtra("dims");
        String title = getResources().getStringArray(R.array.unit_converter_categories)[Formulas.getCategoryInt(category)];
        getSupportActionBar().setTitle(title);
        RecyclerView list = findViewById(R.id.modify_currencies_list);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);
        TypedValue accentValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, accentValue, true);
        UnitListAdapter adapter = new UnitListAdapter(getApplicationContext(), dims, Formulas.getCategoryInt(category), pos, value, accentValue.resourceId);
        list.setAdapter(adapter);
    }
}
