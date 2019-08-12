package ru.art2000.calculator;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Objects;

import ru.art2000.calculator.calculator.CalculatorFragment;
import ru.art2000.calculator.currency_converter.CurrencyConverterFragment;
import ru.art2000.calculator.settings.SettingsFragment;
import ru.art2000.calculator.unit_converter.UnitConverterFragment;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;

public class MainActivity extends DayNightActivity {

    boolean doubleBackToExitPressedOnce = false;
    BottomNavigationView navigation;
    Context mContext;
    CurrencyConverterFragment currency_converter = new CurrencyConverterFragment();
    CalculatorFragment calculator = new CalculatorFragment();
    UnitConverterFragment unit_converter = new UnitConverterFragment();
    PreferenceFragmentCompat settings = new SettingsFragment();
    Fragment currentFragment = currency_converter;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @ColorInt
    private int statusBarColor;
    @ColorInt
    private int calculatorStatusBarColor;

    @Override
    protected void onResumeNightModeChanged(int mode) {
        switch (navigation.getSelectedItemId()) {
            case R.id.navigation_calc:
                getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
                break;
            case R.id.navigation_unit:
                getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
                break;
            case R.id.navigation_currency:
                getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
                break;
            case R.id.navigation_settings:
                getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        PrefsHelper.initialSetup(mContext);
        setTheme(PrefsHelper.getAppTheme());
        Window window = getWindow();
        new Thread(() -> CurrencyValuesHelper.getDataFromDB(mContext)).start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);

        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, currency_converter, "CurrencyConverterFragment")

                .add(R.id.fragment_container, calculator, "CalculatorFragment")
                .hide(calculator)
                .add(R.id.fragment_container, unit_converter, "UnitConverterFragment")
                .hide(unit_converter)
                .add(R.id.fragment_container, settings, "SettingsFragment")
                .hide(settings)

                .commit();

        navigation.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_currency:
                    currency_converter.scrollToTop();
                    break;
                case R.id.navigation_calc:
                    SlidingUpPanelLayout.PanelState state = SlidingUpPanelLayout.PanelState.EXPANDED;
                    if (calculator.panel.getPanelState() == state)
                        state = SlidingUpPanelLayout.PanelState.COLLAPSED;
                    calculator.panel.setPanelState(state);
                    break;
            }
        });
        navigation.setOnNavigationItemSelectedListener(item -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            PrefsHelper.setDefaultTab(this, item.getOrder());
            switch (item.getItemId()) {
                default:
                case R.id.navigation_calc:
                    getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
                    fragmentManager
                            .beginTransaction()
                            .hide(currentFragment)
                            .show(calculator)
                            .runOnCommit(() -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    window.setStatusBarColor(calculatorStatusBarColor);
                            })
                            .commit();
                    currentFragment = calculator;
                    break;
                case R.id.navigation_unit:
                    getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
                    if (PrefsHelper.isUnitViewChanged()) {
                        unit_converter.setNewAdapter();
                    }
                    fragmentManager
                            .beginTransaction()
                            .hide(currentFragment)
                            .show(unit_converter)
                            .runOnCommit(() -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    window.setStatusBarColor(statusBarColor);
                            })
                            .commit();
                    currentFragment = unit_converter;
                    break;
                case R.id.navigation_currency:
                    getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
                    Log.d("CurrencyTR", "start");
                    fragmentManager
                            .beginTransaction()
                            .hide(currentFragment)
                            .show(currency_converter)
                            .runOnCommit(() -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    window.setStatusBarColor(statusBarColor);

                                Log.d("CurrencyTR", "mend");
                            })
                            .commit();
                    currentFragment = currency_converter;
                    break;
                case R.id.navigation_settings:
                    getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
                    fragmentManager
                            .beginTransaction()
                            .hide(currentFragment)
                            .show(settings)
                            .runOnCommit(() -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    window.setStatusBarColor(statusBarColor);
                            })
                            .commit();
                    currentFragment = settings;
                    break;
            }
            return true;
        });
        int tabId;
        if (Objects.requireNonNull(getIntent().getAction()).equals("android.intent.action.MAIN")) {
            tabId = PrefsHelper.getDefaultNavItem();
        } else {
            switch (getIntent().getAction()) {
                default:
                case "ru.art2000.calculator.action.CALCULATOR":
                    tabId = R.id.navigation_calc;
                    break;
                case "ru.art2000.calculator.action.CONVERTER":
                    tabId = R.id.navigation_unit;
                    break;
                case "ru.art2000.calculator.action.CURRENCIES":
                    tabId = R.id.navigation_currency;
                    break;
                case "ru.art2000.calculator.action.SETTINGS":
                    tabId = R.id.navigation_settings;
                    break;
            }
        }
        if (tabId == R.id.navigation_currency) {

        }
        navigation.setSelectedItemId(tabId);
    }

    @Override
    protected void onPause() {
        if (currency_converter.adapter != null)
            currency_converter.adapter.removeEditText2();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() == R.id.navigation_calc && (
                calculator.panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        calculator.panel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED))
            calculator.panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(mContext, R.string.twice_tap_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() ->
                    doubleBackToExitPressedOnce = false, 2000);
        }
    }

}