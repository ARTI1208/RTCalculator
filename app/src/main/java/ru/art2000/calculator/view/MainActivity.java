package ru.art2000.calculator.view;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.ActivityMainBinding;
import ru.art2000.calculator.view.calculator.CalculatorFragment;
import ru.art2000.calculator.view.currency.CurrencyConverterFragment;
import ru.art2000.calculator.view.settings.SettingsFragment;
import ru.art2000.calculator.view.unit.UnitConverterFragment;
import ru.art2000.extensions.activities.AutoThemeActivity;
import ru.art2000.helpers.AndroidHelper;
import ru.art2000.helpers.PrefsHelper;

public class MainActivity extends AutoThemeActivity {

    ActivityMainBinding viewBinding;
    private boolean doubleBackToExitPressedOnce = false;

    private CurrencyConverterFragment currencyConverterFragment;
    private CalculatorFragment calculatorFragment;
    private UnitConverterFragment unitConverterFragment;
    private SettingsFragment settingsFragment;

    @ColorInt
    private int normalStatusBarColor;
    @ColorInt
    private int calculatorStatusBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PrefsHelper.initialSetup(this);
        super.onCreate(savedInstanceState);

        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

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
                AndroidHelper.getColorAttribute(this, R.attr.calculatorInputBackground);

        viewBinding.navigation.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_calc) {
                calculatorFragment.ensureHistoryPanelClosed();
            }
        });

        viewBinding.navigation.setOnNavigationItemSelectedListener(item -> {
            PrefsHelper.setDefaultTab(item.getOrder());

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

        viewBinding.navigation.setupWithViewPager2(
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
        viewBinding.navigation.setSelectedItemId(tabId);
    }

    @Override
    public void onBackPressed() {
        if (viewBinding.navigation.getSelectedItemId() != R.id.navigation_calc
                || calculatorFragment.ensureHistoryPanelClosed()) {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.twice_tap_exit, Toast.LENGTH_SHORT).show();
            new Handler(getMainLooper()).postDelayed(() ->
                    doubleBackToExitPressedOnce = false, 2000);
        }
    }

    public void updateUnitView() {
        unitConverterFragment.updateAdapter();
    }

    @SuppressLint("NewApi")
    private void changeStatusBarColor(boolean isCalculatorPage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isDarkThemeApplied())) {
            if (isCalculatorPage) {
                getWindow().setStatusBarColor(calculatorStatusBarColor);
            } else {
                getWindow().setStatusBarColor(normalStatusBarColor);
            }
        }
    }
}