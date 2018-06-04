package ru.art2000.calculator;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import java.lang.Math;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    public View CalcView;
    public View UnitView;
    public View CurrencyView;
    Button Button_pressed;
    TextView InputTV;
    TextView ResultTV;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEd;
    FragmentManager fragmentManager;
    Fragment fragmentc;
    String buttonText;
    static final int PAGE_COUNT = 8;

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String prefstr;
        Boolean prefbl;


//        pager = (ViewPager) findViewById(R.id.pager);
//        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
//        pager.setAdapter(pagerAdapter);
//
//        pager.setOnPageChangeListener(new OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset,
//                                       int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });


        pager = findViewById(R.id.pager);

        fragmentManager = getFragmentManager();
        fragmentc = new SettingsFragment();
//        fragmentc = new CalculatorFragment();
//        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentc, "CalcFragment").commit();

        // Получаем ссылку на второй фрагмент по ID
//        fragmentc = (Fragment) fragmentManager.findFragmentById(R.id.calc_fragment);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefstr = prefs.getString("app_theme", "light");

//        fragmentc.t
        if (prefs.getBoolean("is_first_run", true)){
            prefsEd = prefs.edit().putBoolean("is_first_run", true);
            prefsEd.apply();
        }
//        if (prefstr.equals("dark"))
//            setTheme(R.style.AppTheme_Dark);
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
        CalcView.setVisibility(View.VISIBLE);
//        InputTV = findViewById(R.id.tv_input);
//        ResultTV = findViewById(R.id.tv_result);
//        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalculatorFragment()).commit();
//                CalcView = findViewById(R.id.calc_layout);
//        UnitView = findViewById(R.id.unit_layout);
//        CurrencyView = findViewById(R.id.currency_layout);
//        CalcView.setVisibility(View.VISIBLE);
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


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

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
//                    fragmentc = new CalculatorFragment();
//                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentc, "CalcFragment").commit();
//                    setListener(new CalculatorFragment());
                    return true;
                case R.id.navigation_unit:
                    CalcView.setVisibility(View.GONE);
                    UnitView.setVisibility(View.VISIBLE);
                    CurrencyView.setVisibility(View.GONE);
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                    getFragmentManager().beginTransaction().remove(fragmentc).commit();
                    createUnitView();
//                    fragmentc = new UnitFragment();
//                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentc, "UnitFragment").commit();
                    return true;
                case R.id.navigation_currency:
                    CalcView.setVisibility(View.GONE);
                    UnitView.setVisibility(View.GONE);
                    CurrencyView.setVisibility(View.VISIBLE);
                    window.setStatusBarColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                    getFragmentManager().beginTransaction().remove(fragmentc).commit();
//                    fragmentc = new CurrencyFragment();
//                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentc, "CurrencyFragment").commit();
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


        // Set the adapter onto the view pager
        pager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }


    private void setupViewPager(ViewPager viewPager) {
//        Adapter adapter = new Adapter(fragmentManager.getFragments().);
//        adapter.addFragment(new Tab1Fragment(), "PHOTOS");
//        adapter.addFragment(new Tab2Fragment(), "HI-FIVES");
//        pager.setAdapter(adapter);
    }

//    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
//
//        public MyFragmentPagerAdapter(FragmentManager fm1) {
//            super(fm1);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return CalculatorFragment.newInstance(position);
//        }
//
//        @Override
//        public int getCount() {
//            return PAGE_COUNT;
//        }
//
//    }

}

