package ru.art2000.calculator.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.art2000.calculator.FirstSetup;
import ru.art2000.calculator.Helper;
import ru.art2000.calculator.R;
import ru.art2000.extensions.DayNightActivity;

public class InfoActivity extends DayNightActivity {

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstance) {
        mContext = getApplicationContext();
        setTheme(PrefsHelper.getAppTheme());
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_app_info);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GradientDrawable back = new GradientDrawable();
        back.setShape(GradientDrawable.RECTANGLE);
        TypedValue changelog_back = new TypedValue();
        getTheme().resolveAttribute(R.attr.calc_input_bg, changelog_back, true);
        int back_color = ContextCompat.getColor(mContext, changelog_back.resourceId);
        back.setColor(back_color);
        back.setCornerRadius(Helper.dip2px(this, 8));
        findViewById(R.id.chng_back).setBackground(back);
        TextView changelog = findViewById(R.id.changelog);
        changelog.setText(getChangeLogText());
        GradientDrawable gd = new GradientDrawable();
        TypedValue accentTypedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, accentTypedValue, true);
        int accentColor = ContextCompat.getColor(mContext, accentTypedValue.resourceId);
        gd.setStroke(Helper.dip2px(this, 4), accentColor);
        gd.setShape(GradientDrawable.OVAL);
        TextView tv = findViewById(R.id.tvv);
        tv.setBackground(gd);
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(this, FirstSetup.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    String getChangeLogText() {
        StringBuilder sb = new StringBuilder();
        InputStream stream = getResources().openRawResource(R.raw.changelog);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String mLine;
            int i = 0;
            while ((mLine = reader.readLine()) != null) {
                if (i == 0)
                    sb.append(mLine);
                else
                    sb.append("\n").append(mLine);
                i++;
            }
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error loading changelog", Toast.LENGTH_SHORT).show();
        }
        return sb.toString();
    }
}
