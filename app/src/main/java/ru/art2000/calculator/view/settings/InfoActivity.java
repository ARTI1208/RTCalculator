package ru.art2000.calculator.view.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.art2000.calculator.R;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.extensions.TextImageView;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.PrefsHelper;

public class InfoActivity extends DayNightActivity {

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstance) {
        mContext = this;
        setTheme(PrefsHelper.getAppTheme());
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_app_info);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GradientDrawable changelogBackgroundDrawable = new GradientDrawable();
        changelogBackgroundDrawable.setShape(GradientDrawable.RECTANGLE);
        int changelogBackgroundColor =
                AndroidHelper.getColorAttribute(mContext, R.attr.calc_input_bg);
        changelogBackgroundDrawable.setColor(changelogBackgroundColor);
        changelogBackgroundDrawable.setCornerRadius(AndroidHelper.dip2px(this, 8));
        findViewById(R.id.chng_back).setBackground(changelogBackgroundDrawable);
        TextView changelog = findViewById(R.id.changelog);
        changelog.setText(getChangeLogText());

        TextImageView devAvatar = findViewById(R.id.dev_avatar);
        devAvatar.getImageView().setImageResource(R.drawable.dev_avatar);
        TextView devName = devAvatar.getTextView();
        devName.setText("ARTI1208");
        devName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        GradientDrawable devBackground = new GradientDrawable();
        devBackground.setStroke(AndroidHelper.dip2px(this, 1), changelogBackgroundColor);
        devBackground.setCornerRadius(AndroidHelper.dip2px(this, 10));
        devAvatar.setBackground(devBackground);

        TextImageView githubButton = findViewById(R.id.github_button);
        githubButton.getImageView().setImageResource(R.drawable.github);
        githubButton.getTextView().setText("GitHub");
        githubButton.setOnClickListener(view -> {
            Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ARTI1208"));
            startActivity(githubIntent);
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
