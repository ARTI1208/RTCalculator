package ru.art2000.calculator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public View CalcView;
    public View UnitView;
    public View CurrencyView;
    Button Button_pressed;
    Boolean unit_view;
    TextView InputTV;
    TextView ResultTV;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEd;
    FragmentManager fragmentManager;
    Fragment fragmentc;
    String buttonText;
    boolean doubleBackToExitPressedOnce = false;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String prefstr;
        Boolean prefbl;
        pager = findViewById(R.id.pager);
        fragmentManager = getFragmentManager();
        fragmentc = new SettingsFragment();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefstr = prefs.getString("app_theme", "light");
        unit_view = prefs.getBoolean("unit_view", true);
        if (prefs.getBoolean("is_first_run", true)){
            prefsEd = prefs.edit().putBoolean("is_first_run", true);
            prefsEd.apply();
        }
        switch (prefstr){
            case "dark":
                setTheme(R.style.AppTheme_Dark);
                break;
            case "daynight":
                setTheme(R.style.AppTheme_DayNight);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalcView = findViewById(R.id.calc_layout);
        UnitView = findViewById(R.id.unit_layout);
        CurrencyView = findViewById(R.id.currency_layout);
        pager = UnitView.findViewById(R.id.pager);
        InputTV = findViewById(R.id.tv_input);
        ResultTV = findViewById(R.id.tv_result);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getBaseContext(),R.color.inputBack));
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        prefstr = prefs.getString("tab_default", "calc_tab");
        switch (prefstr){
            case "calc_tab":
                navigation.setSelectedItemId(R.id.navigation_calc);
                break;
            case "currency_tab":
                navigation.setSelectedItemId(R.id.navigation_currency);
                break;
            case "unit_tab":
                navigation.setSelectedItemId(R.id.navigation_unit);
                break;
            case "settings_tab":
                navigation.setSelectedItemId(R.id.navigation_settings);
                break;
        }
        prefbl = prefs.getBoolean("theme_changed", false);
        if (prefbl) {
            navigation.setSelectedItemId(R.id.navigation_settings);
            prefsEd = prefs.edit().putBoolean("theme_changed", false);
            prefsEd.apply();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.twice_tap_exit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("is_first_run", true)){
            prefsEd = prefs.edit().putBoolean("is_first_run", false);
            prefsEd.apply();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("app_theme", "light").equals("dark"))
            setTheme(R.style.AppTheme_Dark);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            switch (item.getItemId()) {
                case R.id.navigation_calc:
                    CalcView.setVisibility(View.VISIBLE);
                    UnitView.setVisibility(View.GONE);
                    CurrencyView.setVisibility(View.GONE);
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.inputBack));
                    getFragmentManager().beginTransaction().remove(fragmentc).commit();
                    return true;
                case R.id.navigation_unit:
                    CalcView.setVisibility(View.GONE);
                    UnitView.setVisibility(View.VISIBLE);
                    CurrencyView.setVisibility(View.GONE);
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    getFragmentManager().beginTransaction().remove(fragmentc).commit();
                    createUnitView();
                    return true;
                case R.id.navigation_currency:
                    CalcView.setVisibility(View.GONE);
                    UnitView.setVisibility(View.GONE);
                    CurrencyView.setVisibility(View.VISIBLE);
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                    getFragmentManager().beginTransaction().remove(fragmentc).commit();
                    return true;
                case R.id.navigation_settings:
                    CalcView.setVisibility(View.GONE);
                    UnitView.setVisibility(View.GONE);
                    CurrencyView.setVisibility(View.GONE);
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentc, "SettingsFragment").commit();
                    return true;
            }
            return false;
        }
    };

    public void CalcBtnClick(View v){
        Button_pressed = findViewById(v.getId());
        buttonText = Button_pressed.getText().toString();
        CalculationClass calcClass = new CalculationClass();
        calcClass.getButtonType(v, Button_pressed, buttonText, InputTV, ResultTV);
    }

    public void createUnitView(){
        UnitClass adapter = new UnitClass(getBaseContext(), getSupportFragmentManager());
        pager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }
}

