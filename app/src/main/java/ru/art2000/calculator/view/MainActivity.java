package ru.art2000.calculator.view;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.view.calculator.CalculatorFragment;
import ru.art2000.calculator.view.currency.CurrencyConverterFragment;
import ru.art2000.calculator.databinding.ActivityMainBinding;
import ru.art2000.calculator.view.settings.SettingsFragment;
import ru.art2000.calculator.view.unit.UnitConverterFragment;
import ru.art2000.extensions.DayNightActivity;
import ru.art2000.extensions.ReplaceableBottomNavigation;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.CurrencyValuesHelper;
import ru.art2000.helpers.PrefsHelper;

public class MainActivity extends DayNightActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private ReplaceableBottomNavigation navigation;
    private Context mContext;
    private CurrencyConverterFragment currencyConverterFragment;
    private CalculatorFragment calculatorFragment;
    private UnitConverterFragment unitConverterFragment;
    private SettingsFragment settingsFragment;
    @ColorInt
    private int normalStatusBarColor;
    @ColorInt
    private int calculatorStatusBarColor;

    ActivityMainBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Date start = new Date();
        mContext = this;
        PrefsHelper.initialSetup(mContext);
        setTheme(PrefsHelper.getAppTheme());
        new Thread(() -> CurrencyValuesHelper.checkCurrencyDBExists(mContext)).start();
        super.onCreate(savedInstanceState);

        Date par = new Date();

        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        Date cntEnd = new Date();
        Log.d("LoadTime3", String.valueOf(cntEnd.getTime() - par.getTime()));

        List<Fragment> list = getSupportFragmentManager().getFragments();
        for (Fragment fragment : list) {
            if (fragment instanceof CurrencyConverterFragment) {
                currencyConverterFragment = (CurrencyConverterFragment) fragment;
            } else if (fragment instanceof CalculatorFragment) {
                calculatorFragment = (CalculatorFragment) fragment;
            } else if (fragment instanceof UnitConverterFragment) {
                unitConverterFragment = (UnitConverterFragment) fragment;
            } else if (fragment instanceof SettingsFragment) {
                settingsFragment = (SettingsFragment) fragment;
            }
        }
        if (currencyConverterFragment == null) {
            currencyConverterFragment = new CurrencyConverterFragment();
        }
        if (calculatorFragment == null) {
            calculatorFragment = new CalculatorFragment();
        }
        if (unitConverterFragment == null) {
            unitConverterFragment = new UnitConverterFragment();
        }
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }

        normalStatusBarColor = AndroidHelper.getColorAttribute(this, R.attr.colorPrimaryDark);
        calculatorStatusBarColor =
                AndroidHelper.getColorAttribute(this, R.attr.calc_input_bg);


        navigation = viewBinding.navigation;

        navigation.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_calc) {
                calculatorFragment.ensureHistoryPanelClosed();
            }
        });

        navigation.setOnNavigationItemSelectedListener(item -> {
            PrefsHelper.setDefaultTab(this, item.getOrder());

            if (item.getItemId() == R.id.navigation_unit) {
                changeStatusBarColor(false);
                getIntent().setAction("ru.art2000.calculator.action.CONVERTER");
            } else if (item.getItemId() == R.id.navigation_currency) {
                changeStatusBarColor(false);
                getIntent().setAction("ru.art2000.calculator.action.CURRENCIES");
            } else if (item.getItemId() == R.id.navigation_settings) {
                changeStatusBarColor(false);
                getIntent().setAction("ru.art2000.calculator.action.SETTINGS");
            } else {
                changeStatusBarColor(true);
                getIntent().setAction("ru.art2000.calculator.action.CALCULATOR");
            }

            return true;
        });

        navigation.setupWithViewPager2(
                this,
                viewBinding.pager2,
                unitConverterFragment, currencyConverterFragment, calculatorFragment, settingsFragment);

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
        navigation.setSelectedItemId(tabId);

        Date end = new Date();
        Log.d("LoadTime", String.valueOf(end.getTime() - start.getTime()));
    }

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() != R.id.navigation_calc
                || calculatorFragment.ensureHistoryPanelClosed()) {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(mContext, R.string.twice_tap_exit, Toast.LENGTH_SHORT).show();
            new Handler(getMainLooper()).postDelayed(() ->
                    doubleBackToExitPressedOnce = false, 2000);
        }
    }

    public void updateUnitView() {
        unitConverterFragment.updateAdapter();
    }

    private void changeStatusBarColor(boolean isCalculatorPage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isCalculatorPage) {
                getWindow().setStatusBarColor(calculatorStatusBarColor);
            } else {
                getWindow().setStatusBarColor(normalStatusBarColor);
            }
        }
    }
}