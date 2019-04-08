package ru.art2000.calculator.settings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.art2000.calculator.FistSetup;
import ru.art2000.calculator.R;

public class InfoActivity extends AppCompatActivity {

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstance){
        mContext = getApplicationContext();
        setTheme(PrefsHelper.getAppTheme());
        super.onCreate(savedInstance);
        setContentView(R.layout.info_layout);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GradientDrawable back = new GradientDrawable();
        back.setShape(GradientDrawable.RECTANGLE);
        TypedValue changelog_back = new TypedValue();
        getTheme().resolveAttribute(R.attr.calc_input_bg, changelog_back, true);
        int back_color = ContextCompat.getColor(mContext, changelog_back.resourceId);
        back.setColor(back_color);
        back.setCornerRadius(dip2px(8));
        findViewById(R.id.chng_back).setBackground(back);
        TextView changelog = findViewById(R.id.changelog);
        changelog.setText(loadChangeLog());

        GradientDrawable gd = new GradientDrawable();
        TypedValue acc = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, acc, true);
        int acci = ContextCompat.getColor(mContext, acc.resourceId);
        gd.setStroke(dip2px(4), acci);
        gd.setShape(GradientDrawable.OVAL);
        TextView tv = findViewById(R.id.tvv);
        tv.setBackground(gd);
        tv.setOnClickListener(v -> {
            Intent intent = new Intent(this, FistSetup.class);
            startActivity(intent);
        });

        findViewById(R.id.fff).setOnClickListener(v -> {
            Notification.Builder notification = new Notification.Builder(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notification.setColor(Color.parseColor("#ff00ff00"));
                notification.addAction(new Notification.Action.Builder(Icon.createWithResource(
                        mContext, R.drawable.ic_calc), "Action",
                        PendingIntent.getActivity(mContext, 666,
                                new Intent(mContext, InfoActivity.class),
                                PendingIntent.FLAG_CANCEL_CURRENT)).build());
            }
            notification.setContentTitle("Test title");
            notification.setSubText("Test sub");
            notification.setSmallIcon(R.drawable.fuu);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(666, notification.build());
        });


    }

    public int dip2px(float dip){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    String loadChangeLog()
    {
        StringBuilder sb = new StringBuilder();
        InputStream stream = getResources().openRawResource(R.raw.changelog);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String mLine;
            int i = 0;
            while ((mLine = reader.readLine()) != null) {
                if (i == 0)
                    sb.append(mLine);
                else
                    sb.append("\n").
                            append(mLine);
                i++;
            }
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error loading changelog", Toast.LENGTH_SHORT).show();
        }
        return sb.toString();
    }
}
